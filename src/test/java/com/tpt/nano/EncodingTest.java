package com.tpt.nano;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;

import junit.framework.TestCase;

public class EncodingTest extends TestCase {

	private static class Animal {
		@Element
		private String name;
		@Attribute
		private int age;
	}
	
	public void testEncodingXML() throws Exception {
		
		Format format = new Format(false, "iso-8859-1");
		
		IWriter xmlWriter = NanoFactory.getXMLWriter(format);
		Animal animal = new Animal();
		animal.name = "哈巴狗";
		animal.age = 10;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		xmlWriter.write(animal, baos);
//		xmlWriter.write(animal, System.out);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		IReader xmlReader = NanoFactory.getXMLReader();
		Animal copy = xmlReader.read(Animal.class, bais);
		
		assertEquals("哈巴狗", copy.name);
		assertEquals(10, copy.age);
		
	}
	
	public void testEncodingJSON() throws Exception {
		
		Format format = new Format(false, "utf-16");
		
		IWriter jsonWriter = NanoFactory.getJSONWriter(format);
		Animal animal = new Animal();
		animal.name = "哈巴狗";
		animal.age = 10;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		jsonWriter.write(animal, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		IReader jsonReader = NanoFactory.getJSONReader(format);
		Animal copy = jsonReader.read(Animal.class, bais);
		
		assertEquals("哈巴狗", copy.name);
		assertEquals(10, copy.age);
		
	}
	
}
