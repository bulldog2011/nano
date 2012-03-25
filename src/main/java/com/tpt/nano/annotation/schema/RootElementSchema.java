package com.tpt.nano.annotation.schema;

/**
 * 
 * This bean stores XML root element information
 * 
 * @author bulldog
 *
 */
public class RootElementSchema {
	
	private String xmlName;

	private String namespace;
	
	/**
	 * Get xml root element information
	 * 
	 * @return xml root element name
	 */
	public String getXmlName() {
		return xmlName;
	}

	/**
	 * Set xml root element name
	 * 
	 * @param xmlName
	 */
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	/**
	 * Get xml root element namespace
	 * 
	 * @return xml root element namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set xml root element namespace
	 * 
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
