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

package com.indra.sofia2.ssapandroid.ssap;

public class SSAPMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Identificador unico de una peticion
	 */
	private String messageId;
	
	/*
	 * Identificador de la session con un SIB
	 */
	private String sessionKey;
	
	/*
	 * Identificador de la ontologia que referencia el mensaje
	 */
	private String ontology;

	/*
	 * Direccion de sentido del mensaje
	 */
	private SSAPMessageDirection direction;
	
	/*
	 * Tipo de mensaje
	 */
	private SSAPMessageTypes messageType;
	/*
	 * Cuerpo del Mensaje
	 */
	private String body;
	
	
	
	public String getMessageId() { 
		return messageId;
	}



	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}



	public String getSessionKey() {
		return sessionKey;
	}



	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}



	public String getOntology() {
		return ontology;
	}



	public void setOntology(String ontology) {
		this.ontology = ontology;
	}



	public SSAPMessageDirection getDirection() {
		return direction;
	}



	public void setDirection(SSAPMessageDirection direction) {
		this.direction = direction;
	}



	public SSAPMessageTypes getMessageType() {
		return messageType;
	}



	public void setMessageType(SSAPMessageTypes messageType) {
		this.messageType = messageType;
	}



	public String getBody() {
		return body;
	}



	public void setBody(String body) {
		this.body = body;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public static SSAPMessage fromJsonTo(String string) {
		return jsonDeserializer.deserialize( string );
	}
	
	public static final flexjson.JSONSerializer jsonSerializer;
	public static final flexjson.JSONDeserializer<SSAPMessage> jsonDeserializer;
	static {
	    jsonSerializer = new flexjson.JSONSerializer().exclude("*.class");
	    jsonDeserializer = new flexjson.JSONDeserializer<SSAPMessage>().use( null, SSAPMessage.class );
	}

	public String toJSON() {
	    return jsonSerializer.serialize(this);
	}

	
}
