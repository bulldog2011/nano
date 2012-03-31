package com.tpt.nano.util;

public class XmlUtil {
	
	public static String getXmlFullname(String namespace, String localName) {
		if (StringUtil.isEmpty(namespace)) {
			return localName;
		} else {
			return "{" + namespace + "}#" + localName;
		}
	}

}
