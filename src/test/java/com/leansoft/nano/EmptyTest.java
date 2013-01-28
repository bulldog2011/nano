package com.leansoft.nano;

import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;

import junit.framework.TestCase;

public class EmptyTest extends TestCase {
	
    private static final String SOURCE = 
	   "<defaultExample name='test'>\n" +
	   "  <text>some text</text>\n"+
	   "</defaultExample>";	
    
    private static class DefaultExample {
    	
    	@Element
    	private List<String> stringList;
    	
    	@Attribute
    	private String name;
    	
    	@Element
    	private String text;
    	
    	public DefaultExample() {
    		
    	}
    	
    	public DefaultExample(String name, String text) {
    		this.name = name;
    		this.text = text;
    	}
    }
    
    private DefaultExample getDefaultExampleFromXML() throws Exception {
		IReader xmlReader = NanoFactory.getXMLReader();
		DefaultExample example = xmlReader.read(DefaultExample.class, SOURCE);
		return example;
    }
    
	public void testDefaultsXML() throws Exception {
		DefaultExample example = this.getDefaultExampleFromXML();
		
		assertEquals("test", example.name);
		assertEquals("some text", example.text);
		assertNull(example.stringList);
		
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		IReader xmlReader = NanoFactory.getXMLReader();
		validate(xmlWriter, xmlReader, example);
	}
	
	private void validate(IWriter writer, IReader reader, DefaultExample example) throws Exception {
		String str = writer.write(example);
		
		example = reader.read(DefaultExample.class, str);
		assertEquals("test", example.name);
		assertEquals("some text", example.text);
		assertNull(example.stringList);
	}
	
	public void testDefaultsJSON() throws Exception {
		DefaultExample example = this.getDefaultExampleFromXML();

		IWriter jsonWriter = NanoFactory.getJSONWriter();
		IReader jsonReader = NanoFactory.getJSONReader();
		validate(jsonWriter, jsonReader, example);
	}
}
