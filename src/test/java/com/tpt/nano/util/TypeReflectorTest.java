package com.tpt.nano.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class TypeReflectorTest extends TestCase {
	
	private static class Dog {
		private Dog() {}
		
		private List<Dog> childs;
		
		private Set friends;
	}
	
	public void testGetConstructor() throws Exception {
		Constructor con = TypeReflector.getConstructor(Dog.class);
		Dog dog = (Dog)con.newInstance();
		assertNotNull(dog);
	}
	
	public void testGetParameterizedType() throws Exception {
		Field[] fields = Dog.class.getDeclaredFields();
		for(Field field : fields) {
			if (field.getName().equals("childs")) {
				assertTrue(TypeReflector.collectionAssignable(field.getType()));
				assertTrue(TypeReflector.isList(field.getType()));
				Class<?> type = TypeReflector.getParameterizedType(field);
				assertEquals(Dog.class, type);
			}
			if (field.getName().equals("friends")) {
				assertTrue(TypeReflector.collectionAssignable(field.getType()));
				assertFalse(TypeReflector.isList(field.getType()));
				Class<?> type = TypeReflector.getParameterizedType(field);
				assertNull(type);
			}
			
		}
	}

}
