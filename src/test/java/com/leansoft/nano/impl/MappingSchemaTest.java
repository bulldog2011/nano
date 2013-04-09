package com.leansoft.nano.impl;

import java.util.Map;

import junit.framework.TestCase;

import com.leansoft.nano.annotation.Default;
import com.leansoft.nano.annotation.Order;
import com.leansoft.nano.exception.MappingException;

public class MappingSchemaTest extends TestCase {
	
	@Default
	public class Dummy {
		
		// no annotation
		public String field6;
		
		@Order(3)
		public String field3;
		
		@Order(4)
		public String field4;
		
		@Order(1)
		public String field1;
		
		@Order(2)
		public String field2;
		
		// no annotation
		public String field5;
	}
	
	public void testOrder() throws MappingException {
		MappingSchema ms = MappingSchema.fromClass(Dummy.class);
		
		Map<String, Object> field2SchemaMapping = ms.getField2SchemaMapping();
		
		int i = 1;
		for(String fieldName : field2SchemaMapping.keySet()) {
			if (fieldName.startsWith("field")) {
				assertEquals("field" + i, fieldName);
				i++;
			}
		}
	}

}
