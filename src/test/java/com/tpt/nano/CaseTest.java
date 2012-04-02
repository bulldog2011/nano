package com.tpt.nano;

import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;
import com.tpt.nano.annotation.*;

public class CaseTest extends TestCase {
	
	   private static final String SOURCE =
			   "<defaultTextList version='ONE'>\n"+
			   "      <textEntry name='a' version='ONE'>Example 1</textEntry>\r\n"+
			   "      <textEntry name='b' version='TWO'>Example 2</textEntry>\r\n"+
			   "      <textEntry name='c' version='THREE'>Example 3</textEntry>\r\n"+
			   "</defaultTextList>";
	   
	   private static class DefaultTextList {
		   
		   @XmlElement(name="textEntry")
		   private List<TextEntry> list;
		   
		   @XmlAttribute
		   private Version version;
		   
		   public TextEntry get(int index) {
			   return list.get(index);
		   }
	   }
	   
	   private static class TextEntry {
		   @XmlAttribute
		   private String name;
		   
		   @XmlAttribute
		   private Version version;
		   
		   @XmlValue
		   private String text;
	   }

	   private enum Version {
		   ONE,
		   TWO,
		   THREE
	   }
	   
	   public void testList() throws Exception {
		   IReader xmlReader = NanoFactory.getXMLReader();
		   // deserialize
		   DefaultTextList list = xmlReader.read(DefaultTextList.class, SOURCE);
		   
		   assertEquals(list.version, Version.ONE);
	       assertEquals(list.get(0).version, Version.ONE);
	       assertEquals(list.get(0).name, "a");
	       assertEquals(list.get(0).text, "Example 1");
	       assertEquals(list.get(1).version, Version.TWO);
	       assertEquals(list.get(1).name, "b");
	       assertEquals(list.get(1).text, "Example 2");
	       assertEquals(list.get(2).version, Version.THREE);
	       assertEquals(list.get(2).name, "c");
	       assertEquals(list.get(2).text, "Example 3");
	       
	       // serialize
	       StringWriter buffer = new StringWriter();
	       IWriter xmlWriter = NanoFactory.getXMLWriter();
	       xmlWriter.write(list, buffer);
	       String text = buffer.toString();
//	       System.out.println(text);
	       
	       // deserialize again
	       list = xmlReader.read(DefaultTextList.class, text);
		   assertEquals(list.version, Version.ONE);
		   assertEquals(list.get(0).version, Version.ONE);
		   assertEquals(list.get(0).name, "a");
		   assertEquals(list.get(0).text, "Example 1");
		   assertEquals(list.get(1).version, Version.TWO);
		   assertEquals(list.get(1).name, "b");
		   assertEquals(list.get(1).text, "Example 2");
		   assertEquals(list.get(2).version, Version.THREE);
		   assertEquals(list.get(2).name, "c");
		   assertEquals(list.get(2).text, "Example 3");
	   }
	   
}
