package com.leansoft.nano.transform;

import java.util.Date;

import com.leansoft.nano.util.ThreadLocalDateFormatter;

/**
 * Transformer between a string and a java.util.Date object
 * 
 * @author bulldog
 *
 */
class DateTransform implements Transformable<Date> {
	
	public static String FULL = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	   
	public static String LONG = "yyyy-MM-dd HH:mm:ss z";
	
	public static String NORMAL = "yyyy-MM-dd z";
	
	public static String SHORT = "yyyy-MM-dd";

	public Date read(String value) throws Exception {
		String pattern = getPattern(value);
		Date date = ThreadLocalDateFormatter.parse(value, pattern);
		return date;
	}

	public String write(Date value) throws Exception {
		String text = ThreadLocalDateFormatter.format(value, FULL);
		return text;
	}
	
	public static String getPattern(String text) {
        int length = text.length();

        if(length > 23) {
           return FULL;
        }
        if(length > 20) {
           return LONG;
        }
        if(length > 11) {
           return NORMAL;
        }
        return SHORT;
	}

}
