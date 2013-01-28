package com.leansoft.nano;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.leansoft.nano.Format;
import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;

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
		IReader xmlReader = NanoFactory.getXMLReader(format);
		
		validate(xmlWriter, xmlReader);
	}
	
	private void validate(IWriter writer, IReader reader) throws Exception {
		Animal animal = new Animal();
		animal.name = "哈巴狗";
		animal.age = 10;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writer.write(animal, baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Animal copy = reader.read(Animal.class, bais);
		
		assertEquals("哈巴狗", copy.name);
		assertEquals(10, copy.age);
	}
	
	public void testEncodingJSON() throws Exception {
		
		Format format = new Format(false, "utf-16");
		IWriter jsonWriter = NanoFactory.getJSONWriter(format);
		IReader jsonReader = NanoFactory.getJSONReader(format);
		
		validate(jsonWriter, jsonReader);	
	}	
}
