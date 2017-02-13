package com.indra.sofia2.ssapandroid.ssap.body;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.SSAPCommandType;

public class SSAPBodyCommandMessage extends SSAPBodyMessage {
	
	private String thinkp;

	private String thinkpInstance;
	
	private SSAPCommandType type;
	
	private Map<String, String> args;

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

	public SSAPCommandType getType() {
		return type;
	}

	public void setType(SSAPCommandType type) {
		this.type = type;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	
	public static SSAPBodyCommandMessage fromJsonToSSAPBodyCommandMessage(
			String json) {
		try {
			return JSON.deserialize(json, SSAPBodyCommandMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonArray(
			Collection<SSAPBodyCommandMessage> collection) {
		try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Collection<SSAPBodyCommandMessage> fromJsonArrayToSSAPBodyCommandMessage(
			String json) {
		try {
			return JSON.deserializeCollection(json, SSAPBodyCommandMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}