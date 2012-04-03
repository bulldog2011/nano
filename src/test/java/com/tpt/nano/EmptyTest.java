package com.tpt.nano;

import java.util.List;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;

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
    
	public void testDefaults() throws Exception {
		IReader xmlReader = NanoFactory.getXMLReader();
		DefaultExample example = xmlReader.read(DefaultExample.class, SOURCE);
		
		assertEquals("test", example.name);
		assertEquals("some text", example.text);
		assertNull(example.stringList);
		
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		String str = xmlWriter.write(example);
		
		example = xmlReader.read(DefaultExample.class, str);
		assertEquals("test", example.name);
		assertEquals("some text", example.text);
		assertNull(example.stringList);
	}

}
