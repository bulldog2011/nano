package com.tpt.nano.util;

public class StringUtil {

	public static boolean isEmpty(String value) {
		return value == null && value.length() == 0;
	}
	
	public static String lowercaseFirstLetter(String value) {
		char[] stringArray = value.toCharArray();
		stringArray[0] = Character.toLowerCase(stringArray[0]);
		return new String(stringArray);
	}
	
}
