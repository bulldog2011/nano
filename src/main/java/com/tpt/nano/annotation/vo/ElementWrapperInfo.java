package com.tpt.nano.annotation.vo;

import java.lang.reflect.Field;

/**
 * This bean stores mapping information between an XML wrapper element and a POJO list field
 * 
 * @author bulldog
 *
 */
public class ElementWrapperInfo {
	private String xmlName;
	
	private String namespace;
	
	private Field field;

	/**
	 * Get xml element name
	 * 
	 * @return xml element name
	 */
	public String getXmlName() {
		return xmlName;
	}

	/**
	 * Set xml element name
	 * 
	 * @param xmlName
	 */
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	/**
	 * Get xml namespace
	 * 
	 * @return
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set xml namespace
	 * 
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Get POJO field
	 * 
	 * @return POJO field
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
}
