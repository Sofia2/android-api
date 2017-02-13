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

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.json.SSAPMessageDeserializer;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyEmptyMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyMessage;
import com.indra.sofia2.ssapandroid.ssap.body.binary.SSAPBinaryMessage;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;


@JsonDeserialize(using = SSAPMessageDeserializer.class)
@JsonPropertyOrder({"body"})
public class SSAPMessage<T extends SSAPBodyMessage> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Identificador unico de una peticion
	 */
	protected String messageId;
	
	/*
	 * Identificador de la session con un SIB
	 */
	protected String sessionKey;
	
	/*
	 * Identificador de la ontologia que referencia el mensaje
	 */
	protected String ontology;

	/*
	 * Direccion de sentido del mensaje
	 */
	protected SSAPMessageDirection direction;
	
	/*
	 * Tipo de mensaje
	 */
	protected SSAPMessageTypes messageType;

	/*
	 * Cuerpo del Mensaje
	 */
	protected T body = (T) new SSAPBodyEmptyMessage();

	
	protected SSAPVersion version;
	
	public SSAPVersion getVersion() {
		return version;
	}
	
	public void setVersion(SSAPVersion version){
		this.version = version;
		body.setVersion(version);
	}
	
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
		body.getData();
		return body.toJson();
	}
	
	public String getBodyLegacy(){
		return body.toJsonVersion();
	}



	public void setBody(String body) {
		try{
			this.body = (T) JSON.deserialize(body, SSAPBodyMessage.class);
			this.body.setVersion(version);
		} catch (IOException e){
			e.printStackTrace();
		}

	}


	public T getBodyAsObject(){
		return body;
	}
	
	public void setBodyAsObject(T bodyObject){
		this.body = bodyObject;
		this.body.setVersion(version);
	}
	
	public JsonNode getBodyAsJsonObject(){
		try {
			return JSON.deserializeToJson(this.body.toJson(),false);
		} catch (IOException e){
			throw new SSAPMessageDeserializationError(e);
		}
	}
	
	public String getBodyAsJsonString(){
		return JSON.jsonizeString(body.toJson());
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public static SSAPMessage fromJsonToSSAPMessage(String json) {
		try {
			return JSON.deserialize(json,SSAPMessage.class);
		} catch (IOException e){
			throw new SSAPMessageDeserializationError(e);
		}
	}
	
/*	public static final flexjson.JSONSerializer jsonSerializer;
	public static final flexjson.JSONDeserializer<SSAPMessage> jsonDeserializer;
	static {
	    jsonSerializer = new flexjson.JSONSerializer().exclude("*.class");
	    jsonDeserializer = new flexjson.JSONDeserializer<SSAPMessage>().use( null, SSAPMessage.class );
	}*/

	public String toJSON() {
		try{
			return JSON.serialize(this);
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
	
	
	public final String toJsonResponse(){
		try {
			return JSON.serialize(this, version);
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}


	public String toString(){
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
