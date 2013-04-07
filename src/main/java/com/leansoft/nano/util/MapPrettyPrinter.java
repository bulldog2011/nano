package com.leansoft.nano.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapPrettyPrinter {
	
	public static String printMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        sb.append('{');
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            sb.append("    ");
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            sb.append(System.getProperty("line.separator"));
        }
        sb.append('}');
        return sb.toString();
	}

}
