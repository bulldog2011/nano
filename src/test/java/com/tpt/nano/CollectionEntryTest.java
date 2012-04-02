package com.tpt.nano;

import java.io.StringWriter;
import java.util.List;

import com.tpt.nano.annotation.XmlAttribute;
import com.tpt.nano.annotation.XmlElement;
import com.tpt.nano.annotation.XmlRootElement;

import junit.framework.TestCase;

public class CollectionEntryTest extends TestCase {
	
    private static final String LIST = 
		   "<?xml version=\"1.0\"?>\n"+
		   "<exampleCollection>\n"+ 
		   "      <substitute id='1'>\n"+
		   "         <text>one</text>  \n\r"+
		   "      </substitute>\n\r"+
		   "      <substitute id='2'>\n"+
		   "         <text>two</text>  \n\r"+
		   "      </substitute>\n"+
		   "      <substitute id='3'>\n"+
		   "         <text>three</text>  \n\r"+
		   "      </substitute>\n"+
		   "</exampleCollection>";
	   
    private static final String PRIMITIVE_LIST = 
		   "<?xml version=\"1.0\"?>\n"+
		   "<examplePrimitiveCollection>\n"+
		   "   <substitute>a</substitute>\n"+
		   "   <substitute>b</substitute>\n"+
		   "   <substitute>c</substitute>\n"+
		   "   <substitute>d</substitute>\n"+
		   "</examplePrimitiveCollection>";
    
    private static class ExamplePrimitiveCollection {
    	
    	@XmlElement(name="substitute")
    	private List<Character> list;
    	
    	public List<Character> getList() {
    		return list;
    	}
    }
    
    private static class Entry {
    	@XmlAttribute
    	private int id;
    	
    	@XmlElement
    	private String text;
    	
    	public String getText() {
    		return text;
    	}
    	
    	public int getId() {
    		return id;
    	}
    	
    }
    
    @XmlRootElement(name="exampleCollection")
    private static class ExampleCollection {
    	
    	@XmlElement(name="substitute")
    	private List<Entry> list;
    	
    	public List<Entry> getList() {
    		return list;
    	}
    }
    
	   
    public void testExampleCollection() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
		
    	ExampleCollection exampleCollection = xmlReader.read(ExampleCollection.class, LIST);
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	StringWriter writer = new StringWriter();
    	xmlWriter.write(exampleCollection, writer);
		String text = writer.toString();
		
		exampleCollection = xmlReader.read(ExampleCollection.class, text);
		
		assertTrue(exampleCollection.list.size() == 3);
		
		Entry e = exampleCollection.list.get(2);
		assertEquals(3, e.id);
		assertEquals("three", e.text);
    }
    
    public void testExamplePrimitiveCollection() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	
    	ExamplePrimitiveCollection examplePrimitiveCollection = xmlReader.read(ExamplePrimitiveCollection.class, PRIMITIVE_LIST);
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	StringWriter writer = new StringWriter();
    	xmlWriter.write(examplePrimitiveCollection, writer);
		String text = writer.toString();
		
		examplePrimitiveCollection = xmlReader.read(ExamplePrimitiveCollection.class, text);
		assertEquals(4, examplePrimitiveCollection.list.size());
		assertEquals(new Character('c'), examplePrimitiveCollection.list.get(2));
    }

}
