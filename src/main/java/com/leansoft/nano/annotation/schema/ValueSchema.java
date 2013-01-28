package com.leansoft.nano.annotation.schema;

import java.lang.reflect.Field;

/**
 * This bean stores mapping between an XML value and a POJO field.
 * 
 * @author bulldog
 *
 */
public class ValueSchema {

	private boolean data;
	
	private Field field;

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

	/**
	 * Get POJO field
	 * 
	 * @return POJO field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Get POJO field
	 * 
	 * @param field
	 */
	public void setField(Field field) {
		this.field = field;
	}
	
}
