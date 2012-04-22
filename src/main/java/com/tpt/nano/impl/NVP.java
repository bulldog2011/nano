package com.tpt.nano.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.tpt.nano.util.FastStack;
import com.tpt.nano.util.StringUtil;

/**
 * Container holding name value pair
 * 
 * @author bulldog
 *
 */
class NVP {
	
	private String name;
	private String value;
	
	public NVP(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public static NVP fromURLEncodedString(String encodedString, String encoding) throws UnsupportedEncodingException {
		String[] nvp = encodedString.split("=");
		if (nvp.length != 2) {
			return null; // ignore
		}
		String n = nvp[0];
		String v = URLDecoder.decode(nvp[1], encoding);
		
		return new NVP(n, v);
	}

	public String toString() {
		return name + "=" + value;
	}
	
	public String toURLEncodedString(String encoding) throws UnsupportedEncodingException {
		return name + "=" + URLEncoder.encode(value, encoding);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public FastStack<String> getNamePartStack() {
		FastStack<String> namePartStack = new FastStack<String>(5);
		if (!StringUtil.isEmpty(name)) {
			String[] parts = name.split("\\.");
			int size = parts.length;
			for(int i = size - 1; i >= 0; i--) {
				if (!StringUtil.isEmpty(parts[i])) {
					namePartStack.push(parts[i]);
				}
			}
		}
		return namePartStack;
	}
}
