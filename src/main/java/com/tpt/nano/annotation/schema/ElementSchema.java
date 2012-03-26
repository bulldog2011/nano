package com.tpt.nano.annotation.schema;

import java.lang.reflect.Field;

/**
 * This bean stores mapping information between an XML element and a POJO field
 * 
 * @author bulldog
 *
 */
public class ElementSchema {
	
	private String xmlName;
	
	private String namespace;
	
	private Field field;
	
	private boolean data;
	
	private boolean list = false;
	
	private boolean wrapperElement = false;
	
	private String entryXmlName;
	
	private Class<?> parameterizedType;
	
	/**
	 * Check if this is a wrapper element for a list.
	 * 
	 * @return true or false
	 */
	public boolean isWrapperElement() {
		return wrapperElement;
	}

	/**
	 * Set if this is a wrapper element for a list.
	 * 
	 * @param wrapperElement
	 */
	public void setWrapperElement(boolean wrapperElement) {
		this.wrapperElement = wrapperElement;
	}

	/**
	 * Get the entry xml name if this is a wrapper element.
	 * 
	 * @return entry xml name
	 */
	public String getEntryXmlName() {
		return entryXmlName;
	}
	
	/**
	 * Set the entry xml name if this is a wrapper element.
	 * 
	 * @param entryXmlName
	 */
	public void setEntryXmlName(String entryXmlName) {
		this.entryXmlName = entryXmlName;
	}

	/**
	 * Check if this is a java.util.List filed, such as List<T>
	 * 
	 * @return true or false
	 */
	public boolean isList() {
		return list;
	}

	/**
	 * Set if this is a java.util.List field or not.
	 * 
	 * @param collection
	 */
	public void setList(boolean list) {
		this.list = list;
	}
	
	/**
	 * Get parameterized type for a java.util.List field.
	 * 
	 * @return parameterized type.
	 */
	public Class<?> getParameterizedType() {
		return this.parameterizedType;
	}
	
	/**
	 * Set parameterized type for a java.util.List field
	 * 
	 * @param type
	 */
	public void setParameterizedType(Class<?> type) {
		this.parameterizedType = type;
	}

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
