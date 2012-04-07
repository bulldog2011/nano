package com.tpt.nano.transform;

import java.util.Date;
import com.tpt.nano.util.ThreadLocalDateFormatter;

/**
 * Transformer between a string and a java.util.Date object
 * 
 * @author bulldog
 *
 */
class DateTransform implements Transformable<Date> {
	
	private static final String FULL = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	   
	private static final String LONG = "yyyy-MM-dd HH:mm:ss z";
	
	private static final String NORMAL = "yyyy-MM-dd z";
	
	private static final String SHORT = "yyyy-MM-dd";

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
