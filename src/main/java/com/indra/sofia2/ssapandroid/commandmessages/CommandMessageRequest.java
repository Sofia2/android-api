package com.indra.sofia2.ssapandroid.commandmessages;

/**
 * Created by mbriceno on 03/01/2018.
 */

import java.io.IOException;
import java.util.Collection;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;

public class CommandMessageRequest {

    private String messageId;
    private CommandType commandType;
    private String commandMessage;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getCommandMessage() {
        return commandMessage;
    }

    public void setCommandMessage(String commandMessage) {
        this.commandMessage = commandMessage;
    }

    public String toJson() {
        try {
            return JSON.serialize(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonArray(Collection<CommandMessageRequest> collection) {
        try {
            return JSON.serializeCollection(collection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandMessageRequest fromJsonToCommandMessageRequest(String json) {
        try {
            return JSON.deserialize(json, CommandMessageRequest.class);
        } catch (IOException e) {
            throw new SSAPMessageDeserializationError(e);
        }
    }

    public static Collection<CommandMessageRequest> fromJsonArrayToCommandMessageRequests(String json) {
        try {
            return JSON.deserializeCollection(json, CommandMessageRequest.class);
        } catch (IOException e) {
            throw new SSAPMessageDeserializationError(e);
        }
    }

}