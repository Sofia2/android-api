package com.indra.sofia2.ssapandroid.kp.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indra.sofia2.ssapandroid.kp.implementations.utils.IndicationTask;
import com.indra.sofia2.ssapandroid.kp.Kp;
import com.indra.sofia2.ssapandroid.kp.Listener4SIBCommandMessageNotifications;
import com.indra.sofia2.ssapandroid.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssapandroid.kp.config.ConnectionConfig;
import com.indra.sofia2.ssapandroid.kp.exceptions.ConnectionConfigException;
import com.indra.sofia2.ssapandroid.kp.implementations.tcpip.connector.IConnectorMessageListener;

public abstract class KpToExtend implements Kp, IConnectorMessageListener {
	
	protected static Log log = LogFactory.getLog(KpToExtend.class);
	
	/**
	 * Session key del kp en la conexión con el SIB
	 */
	protected String sessionKey;
	
	/**
	 * XXTEA algorithm cipher key
	 */
	protected String xxteaCipherKey=null;

	/**
	 * Subscription listeners thread pool
	 */

	private ExecutorService indicationThreadPool;
	
	/**
	 * Registra los listeners para mensaje de notificacion del Sib
	 */
	protected List<Listener4SIBIndicationNotifications> subscriptionListeners=new ArrayList<Listener4SIBIndicationNotifications>();
	
	/**
	 * Registers listeners for raw messages from SIB notifications
	 */
	protected List<Listener4SIBCommandMessageNotifications> subscriptionCommandMessagesListener=new ArrayList<Listener4SIBCommandMessageNotifications>();
	
	/**
	 * Listeners for notifications of Base Commands
	 */
	protected Listener4SIBIndicationNotifications listener4BaseCommandRequestNotifications;
	
	/**
	 * Listeners for notifications of Status request
	 */
	protected Listener4SIBIndicationNotifications listener4StatusControlRequestNotifications;

	/**
	 * Estado conexion
	 */
	protected boolean joined=false;
	
	/**
	 * Configuracion de Conexion
	 */
	protected ConnectionConfig config=null;
	
	/**
	 * subscriptionId for BaseCommandRequest notifications
	 */
	protected String baseCommandRequestSubscriptionId;
	
	
	/**
	 * subscriptionId for StatusCommandRequest notifications
	 */
	protected String statusControlRequestSubscriptionId;

	protected static int ssapResponseTimeout;

	public int getSsapResponseTimeout() {
		return ssapResponseTimeout;
	}

	public void setSsapResponseTimeout(int timeout) {
		ssapResponseTimeout = timeout;
	}
	
	/**
	 * Hace la desconexion del protocolo físico
	 */
	public void setConnectionConfig(ConnectionConfig config) {
		this.config=config;		
	}
	
	
	public KpToExtend(ConnectionConfig config) throws ConnectionConfigException{
		this.config=config;
		if (config == null) {
			throw new ConnectionConfigException("Configuration is null");
		}
		
		config.validateConfig();
	}
	
		
	public boolean isJoined() {
		return this.joined;
	}

	@Override
	public String getSessionKey() {
		return this.sessionKey;
	}
	
	public void setXxteaCipherKey(String cipherKey){
		this.xxteaCipherKey=cipherKey;
	}
	

	@Override
	public void addListener4SIBNotifications(Listener4SIBIndicationNotifications listener) {
		subscriptionListeners.add(listener);		
	}


	@Override
	public void removeListener4SIBNotifications(
			Listener4SIBIndicationNotifications listener) {
		subscriptionListeners.remove(listener);		
	}
	
	@Override
	public void addListener4SIBCommandMessageNotifications(Listener4SIBCommandMessageNotifications listener){
		subscriptionCommandMessagesListener.add(listener);
	}

	
	@Override
	public void removeListener4SIBCommandMessageNotifications(Listener4SIBCommandMessageNotifications listener){
		subscriptionCommandMessagesListener.remove(listener);
	}
	
	
	@Override
	public void setListener4BaseCommandRequestNotifications(Listener4SIBIndicationNotifications listener){
		listener4BaseCommandRequestNotifications=listener;
	}
	
	@Override
	public void setListener4StatusControlRequestNotifications(Listener4SIBIndicationNotifications listener){
		listener4StatusControlRequestNotifications=listener;
	}
	
	protected void executeIndicationTasks(
			Collection<IndicationTask> indicationTasks) {
		for (IndicationTask task : indicationTasks) {
			this.indicationThreadPool.submit(task);
		}
	}

	protected void initializeIndicationPool() {
		this.indicationThreadPool = Executors.newFixedThreadPool(config
				.getSubscriptionListenersPoolSize());
	}
	
	
	protected void destroyIndicationPool() {
		try {
			this.indicationThreadPool.shutdown();
		} catch (Throwable e) {
			try {
				this.indicationThreadPool.shutdownNow();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
