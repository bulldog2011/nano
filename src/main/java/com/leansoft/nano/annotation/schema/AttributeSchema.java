package com.leansoft.nano.annotation.schema;

import java.lang.reflect.Field;

/**
 * This bean stores mapping between an XML attribute and a POJO field
 * 
 * @author bulldog
 *
 */
public class AttributeSchema {
	
	private String xmlName;
	
	private Field field;

	/**
	 * Get the XML name
	 * 
	 * @return name
	 */
	public String getXmlName() {
		return xmlName;
	}

	/**
	 * Set the XML anem
	 * 
	 * @param xmlName
	 */
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	/**
	 * Set the POJO field
	 * 
	 * @return field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Set the POJO field
	 * 
	 * @param field
	 */
	public void setField(Field field) {
		this.field = field;
	}
}
