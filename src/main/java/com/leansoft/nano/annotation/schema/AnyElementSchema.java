package com.leansoft.nano.annotation.schema;

import java.lang.reflect.Field;


/**
 * This bean stores mapping between any XML element and a POJO list field
 * 
 * @author bulldog
 *
 */
public class AnyElementSchema {
	
	private Field field;
	
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

}
