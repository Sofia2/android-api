package com.indra.sofia2.ssapandroid.ssap.binary;

public interface Encoder {

	
	public String encode(byte[] data);
	
	public byte[] decode(String data);
	
}
