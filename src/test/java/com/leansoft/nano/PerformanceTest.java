package com.leansoft.nano;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

import junit.framework.TestCase;

public class PerformanceTest extends TestCase {

	private static final int ITERATIONS = 100; 
	
	public static final String BASIC_ENTRY =
	   "<?xml version=\"1.0\"?>\n"+
	   "<root number='1234' flag='true'>\n"+
	   "   <name>some name</name>  \n\r"+
	   "   <path>some path</path>\n"+
	   "   <constant>some constant</constant>\n"+
	   "   <text>\n"+
	   "        Some example text where some name is replaced\n"+
	   "        with the system property value and the path is\n"+
	   "        replaced with the path some name\n"+
	   "   </text>\n"+
	   "   <child name='first'>\n"+
	   "      <one>this is the first element</one>\n"+
	   "      <two>the second element</two>\n"+
	   "      <three>the third elment</three>\n"+
	   "      <grand-child>\n"+
	   "         <entry-one key='name.1'>\n"+
	   "            <value>value.1</value>\n"+
	   "         </entry-one>\n"+
	   "         <entry-two key='name.2'>\n"+
	   "            <value>value.2</value>\n"+
	   "         </entry-two>\n"+
	   "      </grand-child>\n"+
	   "   </child>\n"+
	   "     <entry key='name.1'>\n"+
	   "        <value>value.1</value>\n"+
	   "     </entry>\n"+
	   "     <entry key='name.2'>\n"+
	   "        <value>value.2</value>\n"+
	   "     </entry>\n"+
	   "     <entry key='name.3'>\n"+
	   "        <value>value.4</value>\n"+
	   "     </entry>\n"+
	   "     <entry key='name.4'>\n"+
	   "        <value>value.4</value>\n"+
	   "     </entry>\n"+
	   "     <entry key='name.5'>\n"+
	   "        <value>value.5</value>\n"+
	   "     </entry>\n"+
	   "</root>";
	
	@RootElement(name="root")
	public static class RootEntry implements Serializable {

		  @Attribute
	      private int number;     

		  @Attribute(name="flag")
	      private boolean bool;
	      
		  @Element
	      private String constant;

		  @Element
	      private String name;

		  @Element
	      private String path;

		  @Element
	      private String text;

		  @Element(name="child")
	      private ChildEntry entry;

		  @Element(name="entry")
	      private List<ElementEntry> list;

	}
	
	@RootElement(name="child")
	public static class ChildEntry implements Serializable {
     
		  @Attribute
	      private String name;   

		  @Element
	      private String one;

		  @Element
	      private String two;

		  @Element
	      private String three;

		  @Element(name="grand-child")
	      private GrandChildEntry grandChild;
		  
	}
	   
	@RootElement(name="grand-child")
	public static class GrandChildEntry implements Serializable {
      
		   @Element(name="entry-one")
	      private ElementEntry entryOne;

		   @Element(name="entry-two")
	      private ElementEntry entryTwo;
	      
	}
	   
	@RootElement(name="entry")
	public static class ElementEntry implements Serializable {

		  @Attribute(name="key")
	      private String name;

		  @Element
	      private String value;      

	}	
	
	private IReader xmlReader = NanoFactory.getXMLReader();
	private IWriter xmlWriter = NanoFactory.getXMLWriter();
	
	private IReader jsonReader = NanoFactory.getJSONReader();
	private IWriter jsonWriter = NanoFactory.getJSONWriter();
	
	public void testCompareToJavaSerializer() throws Exception {
		RootEntry entry = xmlReader.read(RootEntry.class, BASIC_ENTRY);
		ByteArrayOutputStream nanoXmlBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream nanoJsonBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream nanoNvBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream javaBuffer = new ByteArrayOutputStream();
		ObjectOutputStream javaSerializer = new ObjectOutputStream(javaBuffer);
		
		xmlWriter.write(entry, nanoXmlBuffer);
		System.out.println(nanoXmlBuffer.toString());
		
		jsonWriter.write(entry, nanoJsonBuffer);
		System.out.println(nanoJsonBuffer.toString());
		
		entry = xmlReader.read(RootEntry.class, nanoXmlBuffer.toString());
		// some validations
		assertTrue(entry.bool);
		assertTrue(entry.number == 1234);
		assertEquals(entry.constant, "some constant");
		assertEquals(entry.entry.name, "first");
		assertTrue(entry.list.size() == 5);
		
		entry = jsonReader.read(RootEntry.class, nanoJsonBuffer.toString());
		// some validations
		assertTrue(entry.bool);
		assertTrue(entry.number == 1234);
		assertEquals(entry.constant, "some constant");
		assertEquals(entry.entry.name, "first");
		assertTrue(entry.list.size() == 5);
		
		javaSerializer.writeObject(entry);
		
		byte[] nanoXmlByteArray = nanoXmlBuffer.toByteArray();
		byte[] nanoJsonByteArray = nanoJsonBuffer.toByteArray();
		byte[] nanoNvByteArray = nanoNvBuffer.toByteArray();
		byte[] javaByteArray = javaBuffer.toByteArray();

		long timeToReadWithNanoXml = timeToReadWithNanoXML(RootEntry.class, nanoXmlByteArray, ITERATIONS);
		long timeToReadWithNanoJson = timeToReadWithNanoJSON(RootEntry.class, nanoJsonByteArray, ITERATIONS);
		long timeToReadWithJava = timeToReadWithJava(RootEntry.class, javaByteArray, ITERATIONS);
		
		System.out.println("NANO XML took '" +  timeToReadWithNanoXml + "' ms to read " + ITERATIONS + " documents.");
		System.out.println("NANO JSON took '" +  timeToReadWithNanoJson + "' ms to read " + ITERATIONS + " documents.");
		System.out.println("JAVA took '" +  timeToReadWithJava + "' ms to read " + ITERATIONS + " documents.");		
	}
	
	private long timeToReadWithNanoXML(Class<?> type, byte[] xmlBuffer, int count) throws Exception {
		xmlReader.read(RootEntry.class, new ByteArrayInputStream(xmlBuffer));
		long now = System.currentTimeMillis();
		
		for(int i= 0; i < count; i++) {
			xmlReader.read(RootEntry.class, new ByteArrayInputStream(xmlBuffer));
		}
		
		return System.currentTimeMillis() - now;
	}
	
	private long timeToReadWithNanoJSON(Class<?> type, byte[] jsonBuffer, int count) throws Exception {
		jsonReader.read(RootEntry.class, new ByteArrayInputStream(jsonBuffer));
		long now = System.currentTimeMillis();
		
		for(int i= 0; i < count; i++) {
			jsonReader.read(RootEntry.class, new ByteArrayInputStream(jsonBuffer));
		}
		
		return System.currentTimeMillis() - now;
	}
	
	private long timeToReadWithJava(Class<?> type, byte[] buffer, int count) throws Exception {
		ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(buffer));
		stream.readObject();
		long now = System.currentTimeMillis();
		
		for(int i = 0; i < count; i++) {
			new ObjectInputStream(new ByteArrayInputStream(buffer)).readObject();
		}
		
		return System.currentTimeMillis() - now;
	}
	
	public void testBindingXML() throws Exception {
		RootEntry root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
		
		String xmlStr = xmlWriter.write(root);
		
		root = xmlReader.read(RootEntry.class, xmlStr);
		assertTrue(root.bool);
		assertTrue(root.number == 1234);
		assertEquals("some constant", root.constant);
		assertEquals("first", root.entry.name);
		assertTrue(root.list.size() == 5);
	}
	
	public void testBindingJSON() throws Exception {
		RootEntry root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
		
		String xmlStr = jsonWriter.write(root);
		
		root = jsonReader.read(RootEntry.class, xmlStr);
		assertTrue(root.bool);
		assertTrue(root.number == 1234);
		assertEquals("some constant", root.constant);
		assertEquals("first", root.entry.name);
		assertTrue(root.list.size() == 5);
	}
	
	public void testNanoXMLWrite() throws Exception {
		RootEntry entry = xmlReader.read(RootEntry.class, BASIC_ENTRY);
		long start = System.currentTimeMillis();
		
		entry.constant = ">><<"; // this should be escaped
		entry.text = "this is text>> some more<<"; // this should be escaped
		
		for(int i = 0; i < ITERATIONS; i++) {
			xmlWriter.write(entry, new StringWriter());
		}
		
		long duration = System.currentTimeMillis() - start;
		
		System.out.printf("NANO XML took '%s' ms to write %s documents\n", duration, ITERATIONS);
		
		// some validations
		String xmlStr = xmlWriter.write(entry);
		entry = xmlReader.read(RootEntry.class, xmlStr);
		
		assertTrue(entry.bool);
		assertTrue(entry.number == 1234);
		assertEquals(">><<", entry.constant);
		assertEquals("first", entry.entry.name);
		assertTrue(entry.list.size() == 5);
	}
	
	public void testNanoJSONWrite() throws Exception {
		RootEntry entry = xmlReader.read(RootEntry.class, BASIC_ENTRY);
		long start = System.currentTimeMillis();
		
		entry.constant = ">><<"; // this should be escaped
		entry.text = "this is text>> some more<<"; // this should be escaped
		
		for(int i = 0; i < ITERATIONS; i++) {
			jsonWriter.write(entry, new StringWriter());
		}
		
		long duration = System.currentTimeMillis() - start;
		
		System.out.printf("NANO JSON took '%s' ms to write %s documents\n", duration, ITERATIONS);
		
		// some validations
		String xmlStr = jsonWriter.write(entry);
		entry = jsonReader.read(RootEntry.class, xmlStr);
		
		assertTrue(entry.bool);
		assertTrue(entry.number == 1234);
		assertEquals(">><<", entry.constant);
		assertEquals("first", entry.entry.name);
		assertTrue(entry.list.size() == 5);
	}
}
