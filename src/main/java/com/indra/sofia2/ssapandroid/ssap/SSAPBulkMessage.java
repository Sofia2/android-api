/*******************************************************************************
 * Copyright 2013-16 Indra Sistemas S.A.
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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyBulkMessage;
import com.indra.sofia2.ssapandroid.ssap.body.bulk.message.SSAPBodyBulkItem;
import com.indra.sofia2.ssapandroid.ssap.exceptions.UnsupportedSSAPMessageTypeException;

@JsonIgnoreProperties("body_asCollection")
public class SSAPBulkMessage extends SSAPMessage {
	
	private static final long serialVersionUID = 1L;
	
	//private Collection<SSAPBodyBulkItem> body_asCollection;
	public SSAPBulkMessage(){
		//body_asCollection = new ArrayList<SSAPBodyBulkItem>();
		body = new SSAPBodyBulkMessage();
	}
	
	public SSAPBulkMessage addMessage(SSAPMessage ssapMessage) throws UnsupportedSSAPMessageTypeException {
		this.checkMessageType(ssapMessage.getMessageType());
		SSAPBodyBulkItem item=new SSAPBodyBulkItem();
		item.setType(ssapMessage.getMessageType());
		item.setBody(ssapMessage.getBody());
		item.setOntology(ssapMessage.getOntology());
		((SSAPBodyBulkMessage)body).getItems().add(item);
		return this;
		
	}
	
	public void addMessage(List<SSAPMessage> ssapMessages) throws UnsupportedSSAPMessageTypeException {
		for(SSAPMessage ssapMessage:ssapMessages){
			this.checkMessageType(ssapMessage.getMessageType());
			SSAPBodyBulkItem item=new SSAPBodyBulkItem();
			item.setType(ssapMessage.getMessageType());
			item.setBody(ssapMessage.getBody());
			item.setOntology(ssapMessage.getOntology());
			((SSAPBodyBulkMessage)body).getItems().add(item);
		}
	}
	
	private void checkMessageType(SSAPMessageTypes type) throws UnsupportedSSAPMessageTypeException {
		if (type != SSAPMessageTypes.INSERT && type != SSAPMessageTypes.UPDATE && type != SSAPMessageTypes.DELETE) {
			throw new UnsupportedSSAPMessageTypeException(
					"Message type: " + type + " is not supported by SSAPBulkMessage");
		}
	}
	
	private SSAPBulkMessage prepareForSerialization() {
//		try {
//			//this.body = JSON.
//			//this.body = JSON.deserializeCollection(this.body_asCollection, SSAPBodyBulkItem.class);
//			//this.body = new ObjectMapper().writeValueAsString(this.body_asCollection);
//			return this;
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		return null;
	}
	
	@Override
	public String toJSON() {
		try {
			return JSON.serialize(this);
			//return JSON.serialize(this.prepareForSerialization());
			//return new ObjectMapper().writeValueAsString(this.prepareForSerialization());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
