package com.leansoft.nano;

import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.*;

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
	   
	   private DefaultTextList getDefaultTextListFromXML() throws Exception {
		   IReader xmlReader = NanoFactory.getXMLReader();
		   // deserialize
		   DefaultTextList list = xmlReader.read(DefaultTextList.class, SOURCE);
		   return list;
	   }
	   
	   public void testListXML() throws Exception {
		   DefaultTextList list = getDefaultTextListFromXML();
		   
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
	       
		   IWriter xmlWriter = NanoFactory.getXMLWriter();
		   IReader xmlReader = NanoFactory.getXMLReader();
	       validate(xmlWriter, xmlReader, list);
	   }
	   
	   private void validate(IWriter writer, IReader reader, DefaultTextList list) throws Exception {
	       // serialize
	       StringWriter buffer = new StringWriter();
	       writer.write(list, buffer);
	       String text = buffer.toString();
	       
	       // deserialize again
	       list = reader.read(DefaultTextList.class, text);
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
		   DefaultTextList list = getDefaultTextListFromXML();
		   
	       IWriter jsonWriter = NanoFactory.getJSONWriter();
	       IReader jsonReader = NanoFactory.getJSONReader();
	       validate(jsonWriter, jsonReader, list);
	   }
}
