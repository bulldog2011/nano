package com.leansoft.nano;

import java.io.StringWriter;
import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

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
    	
    	@Element(name="substitute")
    	private List<Character> list;
    	
    	public List<Character> getList() {
    		return list;
    	}
    }
    
    private static class Entry {
    	@Attribute
    	private int id;
    	
    	@Element
    	private String text;
    	
    	public String getText() {
    		return text;
    	}
    	
    	public int getId() {
    		return id;
    	}
    	
    }
    
    @RootElement(name="exampleCollection")
    private static class ExampleCollection {
    	
    	@Element(name="substitute")
    	private List<Entry> list;
    	
    	public List<Entry> getList() {
    		return list;
    	}
    }
    
    
    private ExampleCollection getExampleCollectionFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
		
    	ExampleCollection exampleCollection = xmlReader.read(ExampleCollection.class, LIST);
    	
    	return exampleCollection;
    }
	   
    public void testExampleCollectionXML() throws Exception {
		
    	ExampleCollection exampleCollection = getExampleCollectionFromXMLSource();
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	IReader xmlReader = NanoFactory.getXMLReader();
    	
    	validateExampleCollection(xmlWriter, xmlReader, exampleCollection);
    }
    
    private void validateExampleCollection(IWriter writer, IReader reader, ExampleCollection exampleCollection) throws Exception {
    	StringWriter stringWriter = new StringWriter();
    	writer.write(exampleCollection, stringWriter);
		String text = stringWriter.toString();
		System.out.println(text);
		
		exampleCollection = reader.read(ExampleCollection.class, text);
		
		assertTrue(exampleCollection.list.size() == 3);
		
		Entry e = exampleCollection.list.get(2);
		assertEquals(3, e.id);
		assertEquals("three", e.text);
    }
    
    public void testExampleCollectionJSON() throws Exception {
    	ExampleCollection exampleCollection = getExampleCollectionFromXMLSource();
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
		IReader jsonReader = NanoFactory.getJSONReader();
		
    	validateExampleCollection(jsonWriter, jsonReader, exampleCollection);
    }
    
    private ExamplePrimitiveCollection getExamplePrimitiveCollectionFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	
    	ExamplePrimitiveCollection examplePrimitiveCollection = xmlReader.read(ExamplePrimitiveCollection.class, PRIMITIVE_LIST);
    	
    	return examplePrimitiveCollection;
    }
    
    
    public void testExamplePrimitiveCollectionXML() throws Exception {
    	ExamplePrimitiveCollection examplePrimitiveCollection = getExamplePrimitiveCollectionFromXMLSource();
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	IReader xmlReader = NanoFactory.getXMLReader();
    	
    	validateExamplePrimitiveCollection(xmlWriter, xmlReader, examplePrimitiveCollection);
    }
    
    private void validateExamplePrimitiveCollection(IWriter writer, IReader reader, ExamplePrimitiveCollection examplePrimitiveCollection) throws Exception {
    	StringWriter stringWriter = new StringWriter();
    	writer.write(examplePrimitiveCollection, stringWriter);
		String text = stringWriter.toString();
		
		examplePrimitiveCollection = reader.read(ExamplePrimitiveCollection.class, text);
		assertEquals(4, examplePrimitiveCollection.list.size());
		assertEquals(new Character('c'), examplePrimitiveCollection.list.get(2));
    }
    
    public void testExamplePrimitiveCollectionJSON() throws Exception {
    	ExamplePrimitiveCollection examplePrimitiveCollection = getExamplePrimitiveCollectionFromXMLSource();
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
		IReader jsonReader = NanoFactory.getJSONReader();
		
    	validateExamplePrimitiveCollection(jsonWriter, jsonReader, examplePrimitiveCollection);
    }
}
