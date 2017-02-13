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

package com.indra.sofia2.ssapandroid.ssap.body;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonNode;
import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;



@JsonTypeInfo(use=Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(value=SSAPBodyCommandMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyConfigMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyErrorMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyJoinMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyJoinTokenMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyJoinUserAndPasswordMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyLeaveMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyLocationMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyLogMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyOperationMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyQueryMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyQueryWithParamMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyReturnMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyStatusMessage.class),
	@JsonSubTypes.Type(value=SSAPBodySubscribeCommandMessage.class),
	@JsonSubTypes.Type(value=SSAPBodySubscribeMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyUnsubscribeMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyEmptyMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyBulkMessage.class)
	
})
public abstract class SSAPBodyMessage {
	
	protected JsonNode data = JSON.getObjectMapper().createObjectNode().nullNode();
	//protected String dataLegacy;
	
	//protected String data;
	
	
	protected SSAPVersion version = SSAPVersion.LEGACY;
	
	public SSAPVersion getVersion() {
		return version;
	}

	public void setVersion(SSAPVersion version) {
		this.version = version;
	}

	
	public String toJson() {
		try {
			return JSON.serialize(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String toJsonVersion() {
		try {
			return JSON.serialize(this, version);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final String toJsonResponse() {
		try {
			return JSON.serialize(this, version);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	public void setDataJson(JsonNode node) {
		data = node;

//		if(node.isNull())
//			this.dataLegacy = null;
//		else 
//			this.dataLegacy = node.toString();

	}
	
	public void setData(String data) {
		
//		this.dataLegacy = data;
		if(data == null) {
			this.data = JSON.getObjectMapper().createObjectNode().nullNode();
		}
		else if(data == "") {
			this.data = JSON.getObjectMapper().createObjectNode().textNode("");
		}
		else {
			if(version.equals(SSAPVersion.LEGACY)) {
				this.data = JSON.getObjectMapper().createObjectNode().textNode(data);
			}
			else {
				try {
					this.data = JSON.deserializeToJson(data, false);
				} catch (IOException e) {
					this.data = JSON.getObjectMapper().createObjectNode().nullNode();
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@JsonIgnore
    public String getDataAsJsonString() {
    	return getDataJson().toString();
    }
	@JsonIgnore
    public JsonNode getDataAsJsonObject() {
	   return this.getDataJson();
    }
	
	public JsonNode getDataJson() {
		return data;
	}
	
	public String getData() {
//		if(version.equals(SSAPVersion.LEGACY)) {
//			return dataLegacy;
//		}
//		else 
//		{
			if(data.isTextual())
				return data.asText();
			else if(data.isNull())
				return null;
			else
				return data.toString();	
		//}
	}
	
	/**
	 * Añade {} en el String que representa el JSON si no tiene el { al inicio del documento
	 * @param query
	 * @return
	 */
	protected String prepareQuotes(String query){
		String myQuery = query;
		if (myQuery == null) {
			myQuery = "";
		}
		StringBuffer cadena = new StringBuffer(myQuery);
		if (!myQuery.startsWith("{")){
			cadena.insert(0, "{");
			cadena.append("}");
		}
		return cadena.toString();
	}
	
	public static boolean isLegacy(String json) {
		return !json.contains("@type");
	}
	
}
