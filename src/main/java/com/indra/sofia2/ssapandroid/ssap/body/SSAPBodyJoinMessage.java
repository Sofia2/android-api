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
import java.util.Map;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;

/**
 * Implementacion de JoinMessage
 * @author lmgracia
 *
 */

public class SSAPBodyJoinMessage extends SSAPBodyMessage {
	
	/*
	 * Identificador del dispositivo que se loga
	 */
	private String instance;
	
	private Map<String, String> args;

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	
	public Map<String, String> getArgs() {
		return args;
	}
	
    public static SSAPBodyJoinMessage fromJsonToSSAPBodyJoinMessage(String json) {
    	try {
			return JSON.deserialize(json, SSAPBodyJoinMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
    }
    
    public static String toJsonArray(Collection<SSAPBodyJoinMessage> collection) {
    	try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static String toJsonArray(Collection<SSAPBodyJoinMessage> collection, String[] fields) {
    	try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static Collection<SSAPBodyJoinMessage> fromJsonArrayToSSAPBodyJoinMessages(String json) {
    	try {
			return JSON.deserializeCollection(json, SSAPBodyJoinMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
    }

}
