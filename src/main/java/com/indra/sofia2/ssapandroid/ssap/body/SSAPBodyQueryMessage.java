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
import java.util.Collection;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;
import com.indra.sofia2.ssapandroid.ssap.SSAPQueryType;


public class SSAPBodyQueryMessage extends SSAPBodyMessage {

	/*
	 * Sended query 
	 */
	private String query;
	
	/*
	 * Type message
	 */
	private SSAPQueryType queryType;
	
	public void setQuery(String query) {
		this.query = prepareQuotes(query);
	}
	
	public void setQueryType(SSAPQueryType queryType) {
		this.queryType = queryType;
	}
	
	
	
	public String getQuery() {
		return query;
	}

	public SSAPQueryType getQueryType() {
		return queryType;
	}

	public static String toJsonArray(Collection<SSAPBodyQueryMessage> collection) {
		try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static SSAPBodyQueryMessage fromJsonToSSAPBodyQueryMessage(String json) {
		try {
			json = json.replaceAll(SSAPBodyOperationMessage.class.getSimpleName(), SSAPBodyQueryMessage.class.getSimpleName());
			return JSON.deserialize(json, SSAPBodyQueryMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

	public static Collection<SSAPBodyQueryMessage> fromJsonArrayToSSAPBodyQueryMessages(String json) {
		try {
			json = json.replaceAll(SSAPBodyOperationMessage.class.getSimpleName(), SSAPBodyQueryMessage.class.getSimpleName());
			return JSON.deserializeCollection(json, SSAPBodyQueryMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}
}
