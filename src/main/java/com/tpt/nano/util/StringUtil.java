package com.tpt.nano.util;

import java.io.IOException;
import java.io.Writer;

public class StringUtil {

	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}
	
	public static String lowercaseFirstLetter(String value) {
		char[] stringArray = value.toCharArray();
		stringArray[0] = Character.toLowerCase(stringArray[0]);
		return new String(stringArray);
	}
	
	public static void string2Writer(String source, Writer out) throws IOException {
		
		char[] buffer = source.toCharArray();
		for(int i = 0; i < buffer.length; i++) {
			out.append(buffer[i]);
		}
		out.flush();
	}
	
}
