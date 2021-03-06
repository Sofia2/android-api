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
import java.util.Map;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.SSAPQueryType;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;


public class SSAPBodySubscribeMessage extends SSAPBodyMessage {

	/*
	 * Tiempo para el reenvio de la subscripcion en ms
	 */
	private int msRefresh = 0; 
	
	/*
	 * Query enviada
	 */
	private String query;
	
	/*
	 * Type message
	 */
	private SSAPQueryType queryType;
	
	
	/*
	 * Params query key-value
	 */
	private Map <String, String> queryParams ;
		
	public void setQueryParams(Map<String,String> params){
		this.queryParams = params;
	}
	
	public Map<String, String> getQueryParams(){
		return this.queryParams;
	}
	
	
	public void setQuery(String query) {
		if(queryType== SSAPQueryType.NATIVE) {
			this.query = prepareQuotes(query);
		} else {
			this.query=query;
		}
	}
	
	public void setQueryType(SSAPQueryType queryType) {
		this.queryType = queryType;
	}
	
	public int getMsRefresh() {
		return msRefresh;
	}

	public void setMsRefresh(int msRefresh) {
		this.msRefresh = msRefresh;
	}

	public String getQuery() {
		return query;
	}

	public SSAPQueryType getQueryType() {
		return queryType;
	}

	public static SSAPBodySubscribeMessage fromJsonToSSAPBodySubscribeMessage(String json) {
		try {
			return JSON.deserialize(json, SSAPBodySubscribeMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}
}
