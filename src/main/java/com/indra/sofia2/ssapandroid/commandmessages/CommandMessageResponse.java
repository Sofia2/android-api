package com.indra.sofia2.ssapandroid.commandmessages;

/**
 * Created by mbriceno on 03/01/2018.
 */

import java.io.IOException;
import java.util.Collection;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;


public class CommandMessageResponse {

    private String messageId;
    private String clientId;
    private String commandMessage;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCommandMessage() {
        return commandMessage;
    }

    public void setCommandMessage(String commandMessage) {
        this.commandMessage = commandMessage;
    }

    public String toJson() {
        String ret = null;
        try {
            ret = JSON.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static String toJsonArray(Collection<CommandMessageResponse> collection) {
        try {
            return JSON.serializeCollection(collection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandMessageResponse fromJsonToCommandMessageResponse(String json) {
        try {
            return JSON.deserialize(json, CommandMessageResponse.class);
        } catch (IOException e) {
            throw new SSAPMessageDeserializationError(e);
        }
    }

    public static Collection<CommandMessageResponse> fromJsonArrayToCommandMessageResponses(String json) {
        try {
            return JSON.deserializeCollection(json, CommandMessageResponse.class);
        } catch (IOException e) {
            throw new SSAPMessageDeserializationError(e);
        }
    }

}

