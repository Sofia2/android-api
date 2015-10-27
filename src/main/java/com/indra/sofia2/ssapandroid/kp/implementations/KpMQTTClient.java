/*******************************************************************************
 * Copyright 2013-15 Indra Sistemas S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ******************************************************************************/

package com.indra.sofia2.ssapandroid.kp.implementations;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.Topic;

import android.util.Base64;

import com.indra.sofia2.ssapandroid.commadmessages.CommandMessageResponse;
import com.indra.sofia2.ssapandroid.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssapandroid.kp.config.MQTTConnectionConfig;
import com.indra.sofia2.ssapandroid.kp.encryption.XXTEA;
import com.indra.sofia2.ssapandroid.kp.exceptions.ConnectionConfigException;
import com.indra.sofia2.ssapandroid.kp.exceptions.ConnectionToSibException;
import com.indra.sofia2.ssapandroid.kp.exceptions.DisconnectFromSibException;
import com.indra.sofia2.ssapandroid.kp.exceptions.SSAPResponseTimeoutException;
import com.indra.sofia2.ssapandroid.kp.implementations.mqtt.exceptions.MQTTClientNotConfiguredException;
import com.indra.sofia2.ssapandroid.kp.implementations.utils.IndicationTask;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessage;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessageTypes;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinUserAndPasswordMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyReturnMessage;


public class KpMQTTClient extends KpToExtend {
	
	private static final int CLIENT_ID_LENGTH = 23;

	/**
	 * MQTT client to be used by the protocol to connect it to the MQTT server
	 * in SIB
	 */
	private MQTT clientMQTT;
	
	/**
	 * ClientId prefix to be used
	 */
	private String clientIdPrefix;

	/**
	 * MQTT connection between the MQTT client and the MQTT server in SIB
	 */
	private FutureConnection mqttConnection;

	/**
	 * Thread to receive SIB notifications, regardless if it is SSAP or not
	 */
	private SubscriptionThread subscriptionThread;

	/**
	 * Lock to block a request until receive the synchronous response from SIB
	 */
	private final Lock lock = new ReentrantLock();

	/**
	 * Queue to store ssap message responses
	 */
	private final Queue<Callback> callbacks = new ConcurrentLinkedQueue<Callback>();

	/**
	 * Topic for SSAP responses
	 */
	public static final String TOPIC_PUBLISH_PREFIX = "/TOPIC_MQTT_PUBLISH";

	/**
	 * Topic for SSAP INDICATION Messages
	 */
	public static final String TOPIC_SUBSCRIBE_INDICATION_PREFIX = "/TOPIC_MQTT_INDICATION";
	
	/**
	 * Topic for messages received from SIB that are not SSAP
	 */
	public static final String TOPIC_SIB_COMMANDER_PREFIX="/TOPIC_MQTT_SIB_COMMANDER";
	
	
	/**
	 * Constructor
	 * 
	 * @param config
	 * @throws ConnectionConfigException
	 */
	public KpMQTTClient(MQTTConnectionConfig config) throws ConnectionConfigException{
		super(config);	
	}

	/**
	 * Generats a unique MQTT clientID for each session
	 * 
	 * @return
	 */
	private String generateClientId() {
		if (clientIdPrefix == null) {
			return UUID.randomUUID().toString().substring(0, CLIENT_ID_LENGTH);
		} else {
			Random r = new Random();
			return (clientIdPrefix + Integer.toString(Math.abs(r.nextInt()))).substring(0, CLIENT_ID_LENGTH);
		}
	}

	/**
	 * Create a MQTT client and connects it to the MQTT server in SIB
	 */
	@Override
	public void connect() throws ConnectionToSibException {
		
		try {
			this.initializeIndicationPool();
			
			MQTTConnectionConfig cfg = (MQTTConnectionConfig) config;
			
			ssapResponseTimeout = cfg.getSsapResponseTimeout();
			boolean cleanSession = cfg.isCleanSession();
					
			//Creates the client and open a connection to the SIB
			if (clientMQTT==null) {
				clientMQTT = new MQTT();
				this.clientIdPrefix = cfg.getClientIdPrefix();
				// TODO: integrados username y password MQTT	
				String username = cfg.getUser();
				String password = cfg.getPassword();
				if (username != null){
					clientMQTT.setUserName(username);
				}
				if (password != null){
					clientMQTT.setPassword(password);
				}
				clientMQTT.setClientId(generateClientId());	
				clientMQTT.setHost(config.getHostSIB(),config.getPortSIB());
				clientMQTT.setReconnectAttemptsMax(cfg.getReconnectAttemptsMax());
				clientMQTT.setConnectAttemptsMax(cfg.getConnectAttemptsMax());
				clientMQTT.setReconnectDelay(cfg.getReconnectDelay());
				clientMQTT.setReconnectDelay(cfg.getReconnectDelayMax());
				clientMQTT.setReconnectBackOffMultiplier(cfg.getReconnectBackOffMultiplier());
				clientMQTT.setReceiveBufferSize(cfg.getReceiveBufferSize());
				clientMQTT.setSendBufferSize(cfg.getSendBufferSize());
				clientMQTT.setTrafficClass(cfg.getTrafficClass());
				clientMQTT.setMaxReadRate(cfg.getMaxReadRate());
				clientMQTT.setMaxWriteRate(cfg.getMaxWriteRate());
			}
			if (mqttConnection==null ||  !mqttConnection.isConnected()) {
				clientMQTT.setCleanSession(cleanSession);
				mqttConnection = clientMQTT.futureConnection();
				
				int timeout=config.getTimeOutConnectionSIB();
				if (timeout == 0) {
					mqttConnection.connect().await();
				} else {
					mqttConnection.connect().await(timeout, TimeUnit.MILLISECONDS);
				}
			}
			
			
			//Subscribes to the KP in order to receive any kind of SIB notifications
			this.subscribeToNotificationTopics();
			
			
		} catch (URISyntaxException e) {
			
			throw new ConnectionToSibException("Error connecting to SIB on "+config.getHostSIB()+":"+config.getPortSIB(),e);
		}
		catch (Exception e) {
			throw new ConnectionToSibException("Error connecting to SIB on "+config.getHostSIB()+":"+config.getPortSIB(),e);
		}
	}
	
	@Override
	public boolean isConnected() {
		return joined && mqttConnection != null && mqttConnection.isConnected();
	}

	/**
	 * Disconnect the MQTT client from the SIB
	 */
	@Override
	public void disconnect() {

		if (mqttConnection != null) {
			try {

				// Stop the thread that waits for notifications from SIB
				if (this.subscriptionThread != null
						&& !this.subscriptionThread.isStoped()) {
					this.subscriptionThread.myStop();
				}

				// Unsubscribe SIB notifications
				unSubscribeToNotificationTopics();

				// Disconnect the MQTT client
				mqttConnection.kill().await();
				
				// Previously, we used a SIB-timeout-based disconnect().

			} catch (Exception e) {
				log.error("Ignoring Error disconnect:"+e.getMessage());
			} finally {
				joined = false;
				this.destroyIndicationPool();
			}
		}

	}

	/**
	 * Subscribe the MQTT client to the topics that the SIB will use to notify
	 * messages from SIB
	 */
	private void subscribeToNotificationTopics() {
		Future<byte[]> subscribeFuture = null;

		// Subscription to topic for ssap response messages
		try {
			Topic[] topics = { new Topic(TOPIC_PUBLISH_PREFIX+clientMQTT.getClientId().toString(), ((MQTTConnectionConfig)config).getQualityOfService()) };
			subscribeFuture = mqttConnection.subscribe(topics);

			int timeout=config.getTimeOutConnectionSIB();
			if (timeout == 0) {
				subscribeFuture.await();
			} else {
				subscribeFuture.await(timeout, TimeUnit.MILLISECONDS);
			}			
		} catch (Exception e) {
			log.error("Suscription Error: "+e.getMessage());
			throw new ConnectionToSibException(e);
		}

		// Subscription to topic for ssap indication messages
		try {
			Topic[] topics = { new Topic(TOPIC_SUBSCRIBE_INDICATION_PREFIX+clientMQTT.getClientId().toString(), ((MQTTConnectionConfig)config).getQualityOfService()) };
			subscribeFuture = mqttConnection.subscribe(topics);

			int timeout=config.getTimeOutConnectionSIB();
			if (timeout == 0) {
				subscribeFuture.await();
			} else {
				subscribeFuture.await(timeout, TimeUnit.MILLISECONDS);
			}	
		} catch (Exception e) {
			log.error("Suscription Error: "+e.getMessage());
			throw new ConnectionToSibException(e);
		}
		
				
		//Launch a Thread to receive notifications
		if(this.subscriptionThread==null || this.subscriptionThread.isStoped()){
			this.subscriptionThread=new SubscriptionThread();
			this.subscriptionThread.start();
		}

	}

	/**
	 * Unsubscribe the MQTT client to the topics that the SIB will use to notify
	 * messages from SIB
	 */
	private void unSubscribeToNotificationTopics() {
		Future<Void> subscribeFuture = null;

		// Unsubscription to topic for ssap response messages
		try {
			String[] topics = { new String(TOPIC_PUBLISH_PREFIX+clientMQTT.getClientId().toString()) };
			
			subscribeFuture = mqttConnection.unsubscribe(topics);
			int timeout = config.getTimeOutConnectionSIB();
			if (timeout == 0) {
				subscribeFuture.await();				
			}
			else {
				subscribeFuture.await( timeout, TimeUnit.MILLISECONDS );
			}

		} catch (Exception e) {
			log.error("UnSuscription Error: "+e.getMessage());
			throw new DisconnectFromSibException(e);
		}

		// Unsubscription to topic for ssap indication messages
		try {
			String[] topics = { new String(TOPIC_SUBSCRIBE_INDICATION_PREFIX+clientMQTT.getClientId().toString()) };
			subscribeFuture = mqttConnection.unsubscribe(topics);
			int timeout = config.getTimeOutConnectionSIB();
			if (timeout == 0) {
				subscribeFuture.await();				
			}
			else {
				subscribeFuture.await( timeout, TimeUnit.MILLISECONDS );
			}

		} catch (Exception e) {
			log.error("Suscription Error: "+e.getMessage());
			throw new DisconnectFromSibException(e);
		}

	}

	/**
	 * Send a SSAP message to the server, and returns the response
	 */
	@Override
	public SSAPMessage send(SSAPMessage msg) throws ConnectionToSibException {

		// Sends the message to Server
		try {
			Callback callback = new Callback();
			lock.lock();// Blocks until receive the ssap response
			try {
				callbacks.add(callback);
			    
			    //Publish a QoS message
			    //It is not necessary publish topic. The message will be received by the handler in server
			    mqttConnection.publish("", msg.toJSON().getBytes(), ((MQTTConnectionConfig)config).getQualityOfService(), false);
			      
			}
			finally {
			      lock.unlock();
			}
		
			 SSAPMessage responseSsap = SSAPMessage.fromJsonTo(callback.get());
			 
			//If it is a JOIN message and it is ok, unsubscribes from BaseCommandRequest ontology and from StatusControlRequest ontology
			 if(responseSsap.getMessageType() == SSAPMessageTypes.JOIN &&
					 SSAPBodyReturnMessage.fromJsonTo(responseSsap.getBody()).isOk()){
				 SSAPBodyJoinUserAndPasswordMessage.fromJsonTo(msg.getBody()).getInstance();
			 }
			 return responseSsap;		 
			
		} catch (Exception e) {
			log.error("Publication Error: "+e.getMessage());
			throw new ConnectionToSibException(e);
		}
	}

	/**
	 * Send a SSAP message to the server, and returns the response
	 */
	@Override
	public SSAPMessage sendCipher(SSAPMessage msg) throws ConnectionToSibException {
		
		//Sends the message to Server
		try {

			Callback callback = new Callback();
			lock.lock();// Blocks until receive the ssap response
			try {
				callbacks.add(callback);

				byte[] encrypted = XXTEA.encrypt(msg.toJSON().getBytes(),
						this.xxteaCipherKey.getBytes());

				byte[] bCifradoBase64;

				if (msg.getMessageType() == SSAPMessageTypes.JOIN) {
					SSAPBodyJoinUserAndPasswordMessage body = SSAPBodyJoinUserAndPasswordMessage
							.fromJsonTo(msg
									.getBody());

					String kpName = body.getInstance().split(":")[0];

					String completeMessage = kpName.length() + "#" + kpName
							+ Base64.encodeToString( encrypted, Base64.NO_WRAP );

					bCifradoBase64 = completeMessage.getBytes();
				} else {
			    	bCifradoBase64=Base64.encode( encrypted, Base64.NO_WRAP );
				}

				// Publish a QoS message
				// It is not necessary publish topic. The message will be
				// received by the handler in server
				mqttConnection.publish("", bCifradoBase64,
						((MQTTConnectionConfig) config).getQualityOfService(),
						false);

			} finally {
				lock.unlock();
			}

			SSAPMessage responseSsap = SSAPMessage
			 		.fromJsonTo(callback.get());
			 
			//If it is a JOIN message and it is ok, unsubscribes from BaseCommandRequest ontology and from StatusControlRequest ontology
			 if(responseSsap.getMessageType() == SSAPMessageTypes.JOIN &&
					 SSAPBodyReturnMessage.fromJsonTo(responseSsap.getBody()).isOk()){
				 SSAPBodyJoinUserAndPasswordMessage.fromJsonTo(msg.getBody()).getInstance();
			 }
			return responseSsap;

		} catch (Exception e) {
			log.error("Publication Error: "+e.getMessage());
			throw new ConnectionToSibException(e);
		}
	}

	/**
	 * Send to the SIB the response for a CommandMessageRequest 
	 * @param response
	 */
	public void sendCommandResponse(CommandMessageResponse response){
		mqttConnection.publish("", response.toJson().getBytes(), ((MQTTConnectionConfig)config).getQualityOfService(), false);
	}
	
	
	/**
	 * This thread will be continuously running to receive any kind of messages
	 * from SIB
	 * 
	 * @author jfgpimpollo
	 *
	 */
	private class SubscriptionThread extends Thread {

		private Boolean stop = false;

		protected SubscriptionThread() {

		}

		protected void myStop() {
			this.stop = true;
		}

		protected boolean isStoped() {
			return stop;

		}

		@Override
	    public void run() {
				
			stop=false;
			while (!stop){
				try{
					Future<Message> receive = mqttConnection.receive();					
					// verify the reception
					Message message = receive.await();
					
					if ( message != null ) {
						message.ack();
						
						String messageTopic=message.getTopic();
						if(messageTopic.equals(TOPIC_PUBLISH_PREFIX+clientMQTT.getClientId().toString())){//Notification for SSAP response messages
							//gets the message payload (SSAPMessage)
							String payload = new String(message.getPayload());
							
							payload= clearJsonMessage(payload);
							
							SSAPMessage ssapMessage = SSAPMessage.fromJsonTo(payload);
							//Si el mensaje es un JOIN recupera el SessionKey
							try{
								if (ssapMessage.getMessageType().equals(SSAPMessageTypes.JOIN)) {
									String sKey = SSAPBodyReturnMessage.fromJsonTo(ssapMessage.getBody()).getData();
									sessionKey = sKey;
									if( SSAPBodyReturnMessage.fromJsonTo(ssapMessage.getBody()).isOk()&&
											sKey!=null) {
										joined=true;
									}
								}else if (ssapMessage.getMessageType().equals(SSAPMessageTypes.LEAVE) &&
										SSAPBodyReturnMessage.fromJsonTo(ssapMessage.getBody()).isOk()){
									
									joined=false;
								}	
							
								//Notifies the reception to unlock the synchronous waiting
							}catch(Exception e){};
							callbacks.poll().handle(payload);
							
							
						}else if(messageTopic.equals(TOPIC_SUBSCRIBE_INDICATION_PREFIX+clientMQTT.getClientId().toString())){//Notification for ssap indication message
							//gets the message payload (SSAPMessage)
							String payload = new String(message.getPayload());
							
							payload= clearJsonMessage(payload);
							Collection<IndicationTask> tasks = new ArrayList<IndicationTask>();
							
							SSAPMessage ssapMessage = SSAPMessage.fromJsonTo(payload);
							if(ssapMessage.getMessageType() == SSAPMessageTypes.INDICATION){
								
								String messageId=ssapMessage.getMessageId();
								if(messageId!=null){
									//Notifica a los listener de las suscripciones hechas manualmente
					    			for (Iterator<Listener4SIBIndicationNotifications> iterator = subscriptionListeners.iterator(); iterator.hasNext();) {
										Listener4SIBIndicationNotifications listener = iterator.next();
										tasks.add(new IndicationTask(listener, messageId, ssapMessage));
									}
					    			
					    			//Notifica a los listener de las autosuscripciones
					    			if(messageId.equals(baseCommandRequestSubscriptionId) && listener4BaseCommandRequestNotifications!=null){
					    				tasks.add(new IndicationTask(listener4BaseCommandRequestNotifications, messageId, ssapMessage));
					    			}else if(messageId.equals(statusControlRequestSubscriptionId) && listener4StatusControlRequestNotifications!=null){
					    				tasks.add(new IndicationTask(listener4StatusControlRequestNotifications, messageId, ssapMessage));
					    			}
					    			KpMQTTClient.this.executeIndicationTasks(tasks);
					    		}
							}
							
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.error("Error receiving message from SIB: "
							+ e.getMessage());
					callbacks.poll().handle("");
				}

			}
		}
	}
	
	
	private String clearJsonMessage(String payload){
		if(payload.startsWith("{") &&
				payload.endsWith("}") && 
				payload.contains("direction") && 
				payload.contains("sessionKey")){//mensaje no cifrado con XXTEA
			
			
			 return payload;
		}else{
			 byte[] bCifradoBaseado=Base64.decode(payload,Base64.NO_WRAP);
	         
			 for(int i=0;i<bCifradoBaseado.length;i++){
				 bCifradoBaseado[i]=(byte)(bCifradoBaseado[i] & 0xFF);
	         }
			 
			 String clearMessage=new String(XXTEA.decrypt(bCifradoBaseado, this.xxteaCipherKey.getBytes()));
			 
			 return clearMessage;
		}
	}

	@Override
	public void messageReceived(byte[] message) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns the MQTT clientID for this KP
	 * 
	 * @return
	 * @throws MQTTClientNotConfiguredException
	 */
	public String getClientId() throws MQTTClientNotConfiguredException {
		if (this.clientMQTT == null) {
			throw new MQTTClientNotConfiguredException("MQTT client is not configured yet. To configure MQTT client connect it to a MQTT server");
		} else {
			return this.clientMQTT.getClientId().toString();
		}
	}

	/**
	 * Internal class to receive the synchronous notifications for SSAP messages
	 * 
	 * @author jfgpimpollo
	 *
	 */
	static class Callback {

		private final CountDownLatch latch = new CountDownLatch(1);

		private String response;

        String get() {
          try {
            latch.await(ssapResponseTimeout, TimeUnit.MILLISECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          if ( response == null ) {
        	  log.error( "ssap mqtt response timeout for "  + ssapResponseTimeout + "ms"  );
        	  throw new SSAPResponseTimeoutException( "Timeout: " + ssapResponseTimeout + " waiting for SSAP Response" );
          }
          return response;
        }

		void handle(String response) {
			this.response = response;
			latch.countDown();
		}
	}

}
