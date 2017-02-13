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
package com.indra.sofia2.ssapandroid.ssap.body.binary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.binary.Base64;
import com.indra.sofia2.ssapandroid.ssap.binary.Encoder;
import com.indra.sofia2.ssapandroid.ssap.binary.Encoding;
import com.indra.sofia2.ssapandroid.ssap.binary.Mime;
import com.indra.sofia2.ssapandroid.ssap.binary.Storage;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;

public class SSAPBinaryMessage {

	/**
	 * El fichero serializado
	 */
	private String data;
	/**
	 * La metainformación del fichero
	 */
	private SSAPBinaryMediaMessage media;

	public String getData() {
		return data;
	}
	
	@JsonIgnore
    public String getDataAsJsonString() {
    	return JSON.jsonizeString(data);
    }
	@JsonIgnore
    public JsonNode getDataAsJsonObject() {
	    try {
	    	return JSON.deserializeToJson(data, true);
	    } catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	
	public void setData(String data) {
		this.data = data;
	}

	public SSAPBinaryMediaMessage getMedia() {
		return media;
	}

	public byte[] getBinaryData() {
		Encoder encoder = null;
		switch (media.getBinaryEncoding()) {
		case Base64:
			encoder = new Base64();
			break;
		default:
			break;
		}
		return encoder.decode(getData());
	}

	protected SSAPBinaryMessage() {

	}

	public SSAPBinaryMessage(File data, Storage sotrageArea, Encoding binaryEncoding, Mime mime) {
		this.media = new SSAPBinaryMediaMessage(data.getName(), sotrageArea, binaryEncoding, mime.getValue());
		Encoder encoder = null;
		switch (binaryEncoding) {
		case Base64:
			encoder = new Base64();
			break;
		default:
			break;
		}
		try {
			this.setData(encoder.encode(IOUtils.toByteArray(new FileInputStream(this.getData()))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String toJson() {
		try {
			return JSON.serialize(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonArray(Collection<SSAPBinaryMessage> collection) {
		try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static SSAPBinaryMessage fromJsonToSSAPBinaryMessage(String json) {
		try {
			return JSON.deserialize(json, SSAPBinaryMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

	public static Collection<SSAPBinaryMessage> fromJsonArrayToSSAPBinaryMessage(String json) {
		try {
			return JSON.deserializeCollection(json, SSAPBinaryMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

}
