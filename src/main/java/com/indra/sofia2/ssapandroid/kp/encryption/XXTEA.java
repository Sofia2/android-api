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

package com.indra.sofia2.ssapandroid.kp.encryption;

public final class XXTEA {
    
    public final static byte[] KEY = new byte[]{
    (byte)0x789f5645, (byte)0xf68bd5a4,
    (byte)0x81963ffa, (byte)0x458fac58
};
    
    private XXTEA() {
    }
    
   

    /**
     * Encrypt data with key.
     * 
     * @param data
     * @param key
     * @return
     */
    
    public static byte[] encrypt(byte[] data, byte[] key) {
            if (data.length == 0) {
                    return data;
            }
            return toByteArray(encrypt(toIntArray(data, true), toIntArray(key,
                            false)), false);
    }

    /**
     * Decrypt data with key.
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
            if (data.length == 0) {
                    return data;
            }
            return toByteArray(decrypt(toIntArray(data, false), toIntArray(key,
                            false)), true);
    }

    /**
     * Encrypt data with key.
     * 
     * @param v
     * @param k
     * @return
     */
    public static int[] encrypt(int[] v, int[] k) {
    	
            int n = v.length - 1;

            if (n < 1) {
                    return v;
            }
            int[] key = k;
            if (k.length < 4) {
                    key = new int[4];

                    System.arraycopy(k, 0, key, 0, k.length);
            }
            int z = v[n], y = v[0], delta = 0x9E3779B9, sum = 0, e;
            int p, q = 6 + 52 / (n + 1);

            while (q-- > 0) {
                    sum = sum + delta;
                    e = sum >> 2 & 3;
                    for (p = 0; p < n; p++) {
                            y = v[p + 1];
                            z = v[p] += (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
                                            + (key[p & 3 ^ e] ^ z);
                    }
                    y = v[0];
                    z = v[n] += (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
                                    + (key[p & 3 ^ e] ^ z);
            }
            
            return v;
    }

    /**
     * Decrypt data with key.
     * 
     * @param v
     * @param k
     * @return
     */
    public static int[] decrypt(int[] v, int[] k) {
            int n = v.length - 1;

            if (n < 1) {
                    return v;
            }
            int[] key = k;
            if (k.length < 4) {
                    key = new int[4];

                    System.arraycopy(k, 0, key, 0, k.length);
            }
            int z = v[n], y = v[0], delta = 0x9E3779B9, sum, e;
            int p, q = 6 + 52 / (n + 1);

            sum = q * delta;
            while (sum != 0) {
                    e = sum >> 2 & 3;
                    for (p = n; p > 0; p--) {
                            z = v[p - 1];
                            y = v[p] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
                                            + (key[p & 3 ^ e] ^ z);
                    }
                    z = v[n];
                    y = v[0] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
                                    + (key[p & 3 ^ e] ^ z);
                    sum = sum - delta;
            }
            return v;
    }

    /**
     * Convert byte array to int array.
     * 
     * @param data
     * @param includeLength
     * @return
     */
    public static int[] toIntArray(byte[] data, boolean includeLength) {
            int n = (((data.length & 3) == 0) ? (data.length >>> 2)
                            : ((data.length >>> 2) + 1));
            int[] result;

            if (includeLength) {
                    result = new int[n + 1];
                    result[n] = data.length;
                    
            } else {
                    result = new int[n];
            }
            n = data.length;
            for (int i = 0; i < n; i++) {
                    result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
            }
            return result;
    }

    /**
     * Convert int array to byte array.
     * 
     * @param data
     * @param includeLength
     * @return
     */
    private static byte[] toByteArray(int[] data, boolean includeLength) {
            int n = data.length << 2;
            if (includeLength) {
                    int m = data[data.length - 1];
                    if (m > n) {
                            return null;
                    } else {
                            n = m;
                    }

            }
            
            byte[] result = new byte[n];

            for (int i = 0; i < n; i++) {
                    result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
            }
            return result;
    }
    
    
//    public static void main(String args[]) throws Exception{
//    	
//    	byte[] b=XXTEA.encrypt("{\"body\":\"{\\\"instance\\\":\\\"sofiaweb:sofiaweb001\\\",\\\"password\\\":\\\"sofiaweb\\\",\\\"user\\\":\\\"sofiaweb\\\"}\",\"direction\":\"REQUEST\",\"ontology\":null,\"messageType\":\"JOIN\",\"messageId\":null,\"sessionKey\":null}".getBytes(), "key9key8key7key6".getBytes());
//    	
//    	byte[] nuevo=new byte[b.length]; 
//    	for(int i=0;i<b.length;i++){
//    		
//    		System.out.print((b[i] & 0xff)+" ");
//    		nuevo[i]=(byte)(b[i]);
//    	}
//    	System.out.println();
//    	
//    	for(int i=0;i<nuevo.length;i++){
//    		System.out.print(nuevo[i]+" ");
//    	}
//    	
//    	System.out.println();
//    	String baseBcifrado=Base64.encodeBase64String(nuevo);
//    	System.out.println(baseBcifrado);
//    	
//    	
//		//Cifrado
//		//String toEncryt="{\"messageId\":null,\"sessionKey\":null,\"ontology\":null,\"direction\":\"REQUEST\",\"messageType\":\"JOIN\",\"persistenceType\":null,\"body\":\"{\\\"user\\\":\\\"arduino\\\",\\\"password\\\":\\\"arduino\\\"}\"}";
//		//String toEncryt="Jesus fernandez gomez asdfasdfasdfjlll lllllllllllllllllllllllllllllllllaaaaaaaabbc"; //--> 1 caracter mas y se jode
//    	
//    	//Mensaje que no descifra
//    	//String toEncryt=new String("{\"body\":\"{\\\"data\\\":\\\"_id: {$oid : '51d2033d0529d4c413a03cc3'}\\\",\\\"error\\\":null,\\\"ok\\\":true}\",\"direction\":\"RESPONSE\",\"messageId\":null,\"messageType\":\"INSERT\",\"ontology\":\"LuminositySensor\",\"persistenceType\":null,\"sessionKey\":\"b1fe298b-3868-4381-96ea-3f336d92e0d8\"}") ;
// /*   	String toEncryt=new String("{hola me llamo jesus fernandez y esto es una prueba de codificado/decodificado con algoritmo xxtea en Arduino, donde los fallos estan a la orden del dia. amplio un poco el mensaje, que no se sabe exactamente cuando falla esto al descrifar. Lo vuelvo a ampliar un poco mas a ver si con esta ampliacion consigo que falle}");
//    	
//
//
//    	byte[] b=new byte[toEncryt.length()];
//    	for(int i=0;i<toEncryt.length();i++){
//    		b[i]=(byte)toEncryt.charAt(i);
//    	}
//    	
//    	byte[] bCifrado=XXTEA.encrypt(toEncryt.getBytes(), "key0key1key2key3".getBytes());
//    	
//    	System.out.println("############################# Cifrado ###################################");
//    	for(int i=0;i<bCifrado.length;i++){
//    		
//    		System.out.print((int)(bCifrado[i]& 0xFF)+" "); //convierte el dato a unsigned int
//    		bCifrado[i]=(byte)(bCifrado[i]& 0xFF);
//    	}
//    	System.out.println("\n############################# Cifrado ###################################");
//
//		//El problema viene si convierto lo cifrado a String (Base64 podria ser una solucion)
//		String baseBcifrado=Base64.encodeBase64String(bCifrado);
//		
//		
//		//System.out.println(baseBcifrado);
//		//baseBcifrado="OGOcNoudIfHkkmNZ5gBw+TFkRPBbmtHeHNzTBtjYFAOke61m7BC/hLoeAXpLXTSzu0H90GkwYrnGSxrmTsS25AI3UTPPaZEUQdKK/NA3u/Pjw12KGy6+YafGLjOiaEfCaTI46JM36odf24i+JZ6ySUvDCAfJb8abGQBePRluXpfxi113Un/jdebkRVOvRfeuOj/2ss8FOSq98jhGR1KEo2OttyBhxyMCoL4jZbyHaG39z151m+hbZukfdV/RWlrwRADuoILH8ztysbxPlBfw851FRtMqI/E6NSNu3MQtzJ0UV8cP1Leg4yQOzMEdfUvunKXQsPpcMHU3pHGnHNqkRtbPf06OUqwoyWtJMYFroR2WelpxTuMwChGRxV2wCLvwpd7UZTJVrI2CJlBqz65GQtepsVplTUMANcKCK6dNofz1n9VQ";
//		
//		byte[] bCifradoBaseado=Base64.decodeBase64(baseBcifrado);
//		
//		byte[] b2=XXTEA.decrypt(bCifradoBaseado, "key0key1key2key3".getBytes());
//		
//		System.out.println(new String(b2));
//		*/
//					
//	}
}
