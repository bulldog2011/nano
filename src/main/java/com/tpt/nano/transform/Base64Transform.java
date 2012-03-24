package com.tpt.nano.transform;

import com.tpt.nano.util.Base64;

/**
 * Transformer between a base64 encoded string and a byte[]
 * 
 * @author bulldog
 *
 */
public class Base64Transform implements Transformable<byte[]> {

	public byte[] read(String value) throws Exception {
		return Base64.decode(value);
	}

	public String write(byte[] value) throws Exception {
		return Base64.encode(value);
	}

}
