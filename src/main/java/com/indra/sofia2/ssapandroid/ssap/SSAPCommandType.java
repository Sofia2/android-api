package com.indra.sofia2.ssapandroid.ssap;

public enum SSAPCommandType {
	
	STATUS;
	
	public String value() {
        return name();
    }

    public static SSAPCommandType fromValue(String v) {
        return valueOf(v);
    }
}
