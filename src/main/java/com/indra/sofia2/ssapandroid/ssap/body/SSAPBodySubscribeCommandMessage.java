package com.indra.sofia2.ssapandroid.ssap.body;

import java.io.IOException;
import java.util.Collection;

import com.indra.sofia2.ssapandroid.ssap.SSAPCommandType;
import com.indra.sofia2.ssapandroid.json.JSON;

public class SSAPBodySubscribeCommandMessage extends SSAPBodyMessage {
	
	private String thinkp;

	private String thinkpInstance;
	
	private String token;
	
	private SSAPCommandType type;

	public String getThinkp() {
		return thinkp;
	}

	public void setThinkp(String thinkp) {
		this.thinkp = thinkp;
	}

	public String getThinkpInstance() {
		return thinkpInstance;
	}

	public void setThinkpInstance(String thinkpInstance) {
		this.thinkpInstance = thinkpInstance;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SSAPCommandType getType() {
		return type;
	}

	public void setType(SSAPCommandType type) {
		this.type = type;
	}
	
	public static SSAPBodySubscribeCommandMessage fromJsonToSSAPBodySubscribeCommandMessage(
			String json) {
		try {
			return JSON.deserialize(json, SSAPBodySubscribeCommandMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonArray(
			Collection<SSAPBodySubscribeCommandMessage> collection) {
		try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Collection<SSAPBodySubscribeCommandMessage> fromJsonArrayToSSAPBodySubscribeCommandMessage(
			String json) {
		try {
			return JSON.deserializeCollection(json, SSAPBodySubscribeCommandMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
