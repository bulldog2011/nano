package com.leansoft.nano.transform;

import java.util.Arrays;

import com.leansoft.nano.transform.Base64Transform;

import junit.framework.TestCase;

public class Base64TransformTest extends TestCase {
	
	public void testBase64() throws Exception {
		byte[] data = "hello world!".getBytes();
		Base64Transform transform = new Base64Transform();
		
		String value = transform.write(data);
		byte[] copy = transform.read(value);
		System.out.println(new String(copy));

		assertTrue(Arrays.equals(data, copy));
		
		
	}

}
