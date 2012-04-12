package com.tpt.nano.impl;

import java.util.List;
import java.util.Set;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.Value;
import com.tpt.nano.exception.MappingException;
import com.tpt.nano.impl.MappingSchema;

import junit.framework.TestCase;

public class MappingSchemaExceptionTest extends TestCase {
	
	private class Dummy {
		private int d;
		private String s;
	}
	
	private class One {
		// should thrown exception since Attribute annotation can only annotate primitive or frequently used java type.
		// while Dummy is a complex type.
		@Attribute
		private Dummy dummy;
	}
	
	private class Two {
		// should thrown exception since Value annotation can only annotate primitive or frequently used java type.
		// while Dummy is a complex type.
		@Value
		private Dummy dummy;
	}
	
	// should thrown exception since there are more than one Value annotations
	private class Three {
		@Value
		private int a;
		@Value
		private long b;
	}
	
	// should thrown exception since Value and Element annotations can't coexist
	private class Four {
		@Value
		private int a;
		@Attribute
		private long b;
		@Element
		private String s;
	}
	
	private class Five {
		// should thrown exception since set collection is not support by current Nano framework
		@Element
		private Set<Dummy> dummies;
	}
	
	private class Six {
		// should thrown exception since there no parameterized type for List field
		@Element
		private List dummies;
	}
	
	public void testComplexTypeFieldWithAttributeAnnotation() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(One.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	public void testComplexTypeFieldWithValueAnnotation() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(Two.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	public void testMoreThanOneValueFields() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(Three.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	public void testValueAndElementFieldsCoexist() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(Four.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	public void testSetCollectionFeild() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(Five.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	public void testListFieldWithNoParameterizedType() {
		boolean thrown = false;
		
		try {
			MappingSchema.fromClass(Six.class);
		} catch(MappingException me) {
			thrown = true;
		}
		assertTrue(thrown);
	}

}
