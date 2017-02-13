package com.indra.sofia2.ssapandroid.ssap;

public enum SSAPVersion {
	
	LEGACY,
	ONE;
	
	public String value() {
        return name();
    }

    public static SSAPVersion fromValue(String v) {
        return valueOf(v);
    }

}
