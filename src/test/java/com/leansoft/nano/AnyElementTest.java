package com.leansoft.nano;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.annotation.AnyElement;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

import junit.framework.TestCase;

public class AnyElementTest extends TestCase {
	
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
			   "      <three>the third element</three>\n"+
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

				  @AnyElement
			      private List<Object> any;

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
			
			
			public void testXmlElementEntry() throws Exception {
				RootEntry root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
				assertTrue(root.number == 1234);
				assertEquals("some name", root.name);
				assertEquals("some path", root.path);
				assertEquals("some constant", root.constant);
				assertNotNull(root.text);
				
				assertTrue(root.any.size() == 5);
				org.w3c.dom.Element element = (org.w3c.dom.Element) root.any.get(0);
				assertEquals("value.1", element.getTextContent().trim());
				assertEquals("name.1", element.getAttribute("key"));
				
				element = (org.w3c.dom.Element) root.any.get(4);
				assertEquals("value.5", element.getTextContent().trim());
				assertEquals("name.5", element.getAttribute("key"));
				
				ChildEntry ce = root.entry;
				assertEquals("first", ce.name);
				assertEquals("this is the first element", ce.one);
				assertEquals("the second element", ce.two);
				assertEquals("the third element", ce.three);
				
				GrandChildEntry gce = ce.grandChild;
				ElementEntry ee = gce.entryOne;
				assertEquals("name.1", ee.name);
				assertEquals("value.1", ee.value);
				
				ee = gce.entryTwo;
				assertEquals("name.2", ee.name);
				assertEquals("value.2", ee.value);
				
		    	StringWriter stringWriter = new StringWriter();
		    	xmlWriter.write(root, stringWriter);
		    	String xml = stringWriter.toString();
		    	System.out.println(xml);
		    	
		    	// repeat
				root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
				assertTrue(root.number == 1234);
				assertEquals("some name", root.name);
				assertEquals("some path", root.path);
				assertEquals("some constant", root.constant);
				assertNotNull(root.text);
				
				assertTrue(root.any.size() == 5);
				element = (org.w3c.dom.Element) root.any.get(0);
				assertEquals("value.1", element.getTextContent().trim());
				assertEquals("name.1", element.getAttribute("key"));
				
				element = (org.w3c.dom.Element) root.any.get(4);
				assertEquals("value.5", element.getTextContent().trim());
				assertEquals("name.5", element.getAttribute("key"));
				
				ce = root.entry;
				assertEquals("first", ce.name);
				assertEquals("this is the first element", ce.one);
				assertEquals("the second element", ce.two);
				assertEquals("the third element", ce.three);
				
				gce = ce.grandChild;
				ee = gce.entryOne;
				assertEquals("name.1", ee.name);
				assertEquals("value.1", ee.value);
				
				ee = gce.entryTwo;
				assertEquals("name.2", ee.name);
				assertEquals("value.2", ee.value);
			}
			
			public void testPicoBindableEntry() throws Exception {
				RootEntry root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
				List<Object> eeList = new ArrayList<Object>();
				for(int i = 1; i <= 5; i++) {
					ElementEntry ee = new ElementEntry();
					ee.name = "name." + i;
					ee.value = "value." + i;
					eeList.add(ee);
				}
				root.any = eeList;
				
				
		    	StringWriter stringWriter = new StringWriter();
		    	xmlWriter.write(root, stringWriter);
		    	String xml = stringWriter.toString();
		    	System.out.println(xml);
		    	
		    	// repeat
				root = xmlReader.read(RootEntry.class, BASIC_ENTRY);
				assertTrue(root.number == 1234);
				assertEquals("some name", root.name);
				assertEquals("some path", root.path);
				assertEquals("some constant", root.constant);
				assertNotNull(root.text);
				
				assertTrue(root.any.size() == 5);
				org.w3c.dom.Element element = (org.w3c.dom.Element) root.any.get(0);
				assertEquals("value.1", element.getTextContent().trim());
				assertEquals("name.1", element.getAttribute("key"));
				
				element = (org.w3c.dom.Element) root.any.get(4);
				assertEquals("value.5", element.getTextContent().trim());
				assertEquals("name.5", element.getAttribute("key"));
				
				ChildEntry ce = root.entry;
				assertEquals("first", ce.name);
				assertEquals("this is the first element", ce.one);
				assertEquals("the second element", ce.two);
				assertEquals("the third element", ce.three);
				
				GrandChildEntry gce = ce.grandChild;
				ElementEntry ee = gce.entryOne;
				assertEquals("name.1", ee.name);
				assertEquals("value.1", ee.value);
				
				ee = gce.entryTwo;
				assertEquals("name.2", ee.name);
				assertEquals("value.2", ee.value);
			}

}
