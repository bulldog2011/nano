package com.tpt.nano.annotation.vo;

import java.lang.reflect.Field;

/**
 * This bean stores information about a POJO field 
 * which should be ignored by serialization.
 * 
 * @author bulldog
 *
 */
public class IgnoreInfo {
	
	private Field field;

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
