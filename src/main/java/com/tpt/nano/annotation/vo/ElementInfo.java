package com.tpt.nano.annotation.vo;

import java.lang.reflect.Field;

/**
 * This bean stores mapping information between an XML element and a POJO field
 * 
 * @author bulldog
 *
 */
class ElementInfo {
	
	private String xmlName;
	
	private String namespace;
	
	private Field field;
	
	private boolean data;

	/**
	 * Get XML element name
	 * 
	 * @return xml element name
	 */
	public String getXmlName() {
		return xmlName;
	}

	/**
	 * Set XML element name
	 * 
	 * @param xmlName
	 */
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	/**
	 * Get XML namespace
	 * 
	 * @return xml namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set XML namespace
	 * 
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Get POJO field
	 * 
	 * @return field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Set POJO field
	 * 
	 * @param field
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Indicates if the string content of the field should be put 
	 * in a CDATA container on serialization
	 * 
	 * @return true or false
	 */
	public boolean isData() {
		return data;
	}

	/**
	 * Set if the string content of the field should be put 
	 * in a CDATA container on serialization
	 * 
	 * @param data
	 */
	public void setData(boolean data) {
		this.data = data;
	}
}
