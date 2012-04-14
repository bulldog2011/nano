package com.tpt.nano;

import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;
import com.tpt.nano.annotation.*;

public class DefaultTest extends TestCase {
	
	   private static final String SOURCE =
			   "<defaultTextList version='ONE'>\n"+
			   "      <textEntry name='a' version='ONE'>Example 1</textEntry>\r\n"+
			   "      <textEntry name='b' version='TWO'>Example 2</textEntry>\r\n"+
			   "      <textEntry name='c' version='THREE'>Example 3</textEntry>\r\n"+
			   "</defaultTextList>";
	   
	   private static class DefaultTextList {
		   
		   @Element(name="textEntry")
		   private List<TextEntry> list;
		   
		   @Attribute
		   private Version version;
		   
		   public TextEntry get(int index) {
			   return list.get(index);
		   }
	   }
	   
	   private static class TextEntry {
		   @Attribute
		   private String name;
		   
		   @Attribute
		   private Version version;
		   
		   @Value
		   private String text;
	   }

	   private enum Version {
		   ONE,
		   TWO,
		   THREE
	   }
	   
	   public void testListXML() throws Exception {
		   IReader xmlReader = NanoFactory.getXMLReader();
		   // deserialize
		   DefaultTextList list = xmlReader.read(DefaultTextList.class, SOURCE);
		   
		   assertEquals(Version.ONE, list.version);
	       assertEquals(Version.ONE, list.get(0).version);
	       assertEquals("a", list.get(0).name);
	       assertEquals("Example 1", list.get(0).text);
	       assertEquals(Version.TWO, list.get(1).version);
	       assertEquals("b", list.get(1).name);
	       assertEquals("Example 2", list.get(1).text);
	       assertEquals(Version.THREE, list.get(2).version);
	       assertEquals("c", list.get(2).name);
	       assertEquals("Example 3", list.get(2).text);
	       
	       // serialize
	       StringWriter buffer = new StringWriter();
	       IWriter xmlWriter = NanoFactory.getXMLWriter();
	       xmlWriter.write(list, buffer);
	       String text = buffer.toString();
//	       System.out.println(text);
	       
	       // deserialize again
	       list = xmlReader.read(DefaultTextList.class, text);
		   assertEquals(Version.ONE, list.version);
		   assertEquals(Version.ONE, list.get(0).version);
		   assertEquals("a", list.get(0).name);
		   assertEquals("Example 1", list.get(0).text);
		   assertEquals(Version.TWO, list.get(1).version);
		   assertEquals("b", list.get(1).name);
		   assertEquals("Example 2", list.get(1).text);
		   assertEquals(Version.THREE, list.get(2).version);
		   assertEquals("c", list.get(2).name);
		   assertEquals("Example 3", list.get(2).text);
	   }
	   
	   public void testListJSON() throws Exception {
		   IReader xmlReader = NanoFactory.getXMLReader();
		   // deserialize
		   DefaultTextList list = xmlReader.read(DefaultTextList.class, SOURCE);
		   
		   assertEquals(Version.ONE, list.version);
	       assertEquals(Version.ONE, list.get(0).version);
	       assertEquals("a", list.get(0).name);
	       assertEquals("Example 1", list.get(0).text);
	       assertEquals(Version.TWO, list.get(1).version);
	       assertEquals("b", list.get(1).name);
	       assertEquals("Example 2", list.get(1).text);
	       assertEquals(Version.THREE, list.get(2).version);
	       assertEquals("c", list.get(2).name);
	       assertEquals("Example 3", list.get(2).text);
	       
	       // serialize
	       StringWriter buffer = new StringWriter();
	       IWriter jsonWriter = NanoFactory.getJSONWriter();
	       jsonWriter.write(list, buffer);
	       String text = buffer.toString();
	       
	       // deserialize again
	       IReader jsonReader = NanoFactory.getJSONReader();
	       list = jsonReader.read(DefaultTextList.class, text);
		   assertEquals(Version.ONE, list.version);
		   assertEquals(Version.ONE, list.get(0).version);
		   assertEquals("a", list.get(0).name);
		   assertEquals("Example 1", list.get(0).text);
		   assertEquals(Version.TWO, list.get(1).version);
		   assertEquals("b", list.get(1).name);
		   assertEquals("Example 2", list.get(1).text);
		   assertEquals(Version.THREE, list.get(2).version);
		   assertEquals("c", list.get(2).name);
		   assertEquals("Example 3", list.get(2).text);
	   }
}
