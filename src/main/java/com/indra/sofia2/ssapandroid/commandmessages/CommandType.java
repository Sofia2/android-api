package com.indra.sofia2.ssapandroid.commandmessages;

/**
 * Created by mbriceno on 03/01/2018.
 */

public enum CommandType {

    DEVICE_STATUS(1),
    BATERY_SATUS(2);


    private int commandType;

    private CommandType(int commandType) {
        this.commandType = commandType;
    }

    public int getCommandType(){
        return this.commandType;
    }

}