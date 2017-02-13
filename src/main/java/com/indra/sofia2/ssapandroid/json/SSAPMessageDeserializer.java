package com.indra.sofia2.ssapandroid.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessage;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessageDirection;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessageTypes;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyBulkMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyCommandMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyConfigMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyEmptyMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyErrorMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinTokenMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinUserAndPasswordMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyLeaveMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyLocationMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyLogMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyOperationMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyReturnMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyStatusMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodySubscribeCommandMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodySubscribeMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyUnsubscribeMessage;

public class SSAPMessageDeserializer extends StdDeserializer<SSAPMessage<SSAPBodyMessage>>{

	protected SSAPMessageDeserializer(Class<?> vc) {
		super(vc);
	}
	
	protected SSAPMessageDeserializer() {
		this(SSAPMessage.class);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public SSAPMessage<SSAPBodyMessage> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		
		String messageId = node.path("messageId").isNull() ? null : node.path("messageId").asText();
		String sessionKey = node.path("sessionKey").isNull() ? null : node.path("sessionKey").asText();
		String ontology = node.path("ontology").isNull() ? null : node.path("ontology").asText();
		String direction = node.path("direction").asText(SSAPMessageDirection.ERROR.name());
		String messageType = node.path("messageType").asText(SSAPMessageTypes.NONE.name());
		String version;// = node.path("version").asText(SSAPVersion.LEGACY.name());
		
		
		String body;
		JsonNode nodeBody = node.path("body");
		
		if(nodeBody.isObject())
			body = nodeBody.toString();
		else if(nodeBody.isNull()) {
			body = "{}";	
		}
		else {
			body = nodeBody.asText();	
		}
		
		//LEGACY BULK MESSAGES
		if(body.startsWith("["))
			body = "{\"items\":" + body + "}";
		
		//LEGACY MESSAGES COMES WITHOUT @type
		if(!body.contains("@type")) {
			String separator = body.equals("{}") ? "" : ",";
			body = body.replaceFirst("\\{", "{\"@type\":\""+ getClassSimpleNameFromMessageType(SSAPMessageTypes.fromValue(messageType), body) +"\"" + separator);
			
			if(node.path("version").isNull())
				version = SSAPVersion.LEGACY.name();
			else
				version = node.path("version").asText(SSAPVersion.LEGACY.name());
		}
		else {
			if(node.path("version").isNull())
				version = SSAPVersion.ONE.name();
			else
				version = node.path("version").asText(SSAPVersion.ONE.name());
		}
		
		SSAPMessage<SSAPBodyMessage> message = new SSAPMessage<SSAPBodyMessage>();
		message.setMessageId(messageId);
		message.setSessionKey(sessionKey);
		message.setOntology(ontology);
		message.setDirection(SSAPMessageDirection.fromValue(direction));
		message.setMessageType(SSAPMessageTypes.fromValue(messageType));
		message.setVersion(SSAPVersion.fromValue(version));
		
		message.setBody(body);

		return message;
	}
	
	public static boolean hasType(String json) {
		return json.contains("@type");
	}
	
	public static String getClassSimpleNameFromMessageType(SSAPMessageTypes type, String body) {
		String ret;
		switch (type) {
		case JOIN:
			if(body.contains("token"))
				ret = SSAPBodyJoinTokenMessage.class.getSimpleName();
			else if(body.contains("password"))
				ret = SSAPBodyJoinUserAndPasswordMessage.class.getSimpleName();
			else 
				ret = SSAPBodyJoinMessage.class.getSimpleName();
			break;
		case LEAVE:
			ret = SSAPBodyLeaveMessage.class.getSimpleName();
			break;
		case INSERT:
			ret = SSAPBodyOperationMessage.class.getSimpleName();
			break;
		case UPDATE:
			ret = SSAPBodyOperationMessage.class.getSimpleName();
			break;
		case DELETE:
			ret = SSAPBodyOperationMessage.class.getSimpleName();
			break;
		case QUERY:
			ret = SSAPBodyOperationMessage.class.getSimpleName();
			break;
		case BULK:
			ret = SSAPBodyBulkMessage.class.getSimpleName();
			break;
		case SUBSCRIBE:
			ret = SSAPBodySubscribeMessage.class.getSimpleName();
			break;
		case UNSUBSCRIBE:
			ret = SSAPBodyUnsubscribeMessage.class.getSimpleName();
			break;
		case LOG:
			ret = SSAPBodyLogMessage.class.getSimpleName();
			break;
		case LOCATION:
			ret = SSAPBodyLocationMessage.class.getSimpleName();
			break;
		case ERROR:
			ret = SSAPBodyErrorMessage.class.getSimpleName();
			break;
		case COMMAND:
			ret = SSAPBodyCommandMessage.class.getSimpleName();
			break;
		case SUBSCRIBECOMMAND:
			ret = SSAPBodySubscribeCommandMessage.class.getSimpleName();
			break;
		case STATUS:
			ret = SSAPBodyStatusMessage.class.getSimpleName();
			break;
		case CONFIG:
			ret = SSAPBodyConfigMessage.class.getSimpleName();
			break;
		default:
			ret = SSAPBodyEmptyMessage.class.getSimpleName();
			break;
		}
		
		return ret;
	}

}
