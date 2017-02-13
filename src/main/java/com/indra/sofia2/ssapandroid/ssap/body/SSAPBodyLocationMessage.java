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

public class SSAPBodyLocationMessage extends SSAPBodyMessage {

	private String thinkp;

	private String instanciathinkp;
	
	private String token;
	
	private String owner;
	
	private double accuracy;
	
	private double lat;
	
	private double lon;

	private double alt;
	
	private double bearing;
	
	private double speed;
	
	private String timestamp;
	
	public String getThinkp() {
		return thinkp;
	}
	
	public String getInstanciathinkp() {
		return instanciathinkp;
	}

	public void setInstanciathinkp(String instanciathinkp) {
		this.instanciathinkp = instanciathinkp;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setThinkp(String thinkp) {
		this.thinkp = thinkp;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public static SSAPBodyLocationMessage fromJsonToSSAPBodyLocationMessage(
			String json) {
		try {
			return JSON.deserialize(json, SSAPBodyLocationMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

//	public static String toJsonArray(
//			Collection<SSAPBodyLocationMessage> collection) {
//		try {
//			return JSON.serializeCollection(collection);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

//	public static String toJsonArray(
//			Collection<SSAPBodyLocationMessage> collection, String[] fields) {
//		return new JSONSerializer().include(fields).exclude("*.class")
//				.serialize(collection);
//	}

	public static Collection<SSAPBodyLocationMessage> fromJsonArrayToSSAPBodyLocationMessages(
			String json) {
		try {
			return JSON.deserializeCollection(json, SSAPBodyLocationMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

}
