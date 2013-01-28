package com.leansoft.nano;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

import junit.framework.TestCase;

public class TypeTest extends TestCase {

	private static final String SOURCE =
	   "<?xml version=\"1.0\"?>\n"+
	   "<test>\n"+
	   "   <primitive>\n"+
	   "     <boolean>true</boolean>\r\n"+
	   "     <byte>16</byte>  \n\r"+
	   "     <short>120</short>  \n\r"+
	   "     <int>1234</int>\n"+
	   "     <float>1234.56</float>  \n\r"+
	   "     <long>1234567</long>\n"+
	   "     <double>1234567.89</double>  \n\r"+
	   "   </primitive>\n"+
	   "   <object>\n"+
	   "     <Boolean>true</Boolean>\r\n"+
	   "     <Byte>16</Byte>  \n\r"+
	   "     <Short>120</Short>  \n\r"+
	   "     <Integer>1234</Integer>\n"+
	   "     <Float>1234.56</Float>  \n\r"+
	   "     <Long>1234567</Long>\n"+
	   "     <Double>1234567.89</Double>  \n\r"+
	   "     <String>text value</String>\n\r"+
	   "     <Enum>TWO</Enum>\n"+
	   "   </object>\n\r"+
	   "</test>";
	
	@RootElement(name="test")
	private static class Entry {
		@Element
		private PrimitiveEntry primitive;
		
		@Element
		private ObjectEntry object;
	}
	

	private static class PrimitiveEntry {

	      @Element(name="boolean")
	      private boolean booleanValue;            

	      @Element(name="byte")
	      private byte byteValue;

	      @Element(name="short")
	      private short shortValue;

	      @Element(name="int")
	      private int intValue;   

	      @Element(name="float")
	      private float floatValue;

	      @Element(name="long")
	      private long longValue;         

	      @Element(name="double")
	      private double doubleValue;
	   }

	private static class ObjectEntry {

	      @Element(name="Boolean")
	      private Boolean booleanValue;              

	      @Element(name="Byte")
	      private Byte byteValue;

	      @Element(name="Short")
	      private Short shortValue;

	      @Element(name="Integer")
	      private Integer intValue;   

	      @Element(name="Float")
	      private Float floatValue;

	      @Element(name="Long")
	      private Long longValue;         

	      @Element(name="Double")
	      private Double doubleValue;

	      @Element(name="String")
	      private String stringValue;

	      @Element(name="Enum")
	      private TestEnum enumValue;              
	   }  
	
   private static enum TestEnum { 

	   ONE,
	   TWO,
	   THREE
   }
   
   private Entry getEntryFromXML() throws Exception {
	   IReader xmlReader = NanoFactory.getXMLReader();
	   Entry entry = xmlReader.read(Entry.class, SOURCE);
	   return entry;
   }
   
   public void testTypeXML() throws Exception {
	   Entry entry = getEntryFromXML();
	   
       assertEquals(true, entry.primitive.booleanValue);
       assertEquals(16, entry.primitive.byteValue);
       assertEquals(120, entry.primitive.shortValue);
       assertEquals(1234, entry.primitive.intValue);
       assertEquals(1234.56f, entry.primitive.floatValue);
       assertEquals(1234567l, entry.primitive.longValue);
       assertEquals(1234567.89d, entry.primitive.doubleValue);

       assertEquals(Boolean.TRUE, entry.object.booleanValue);
       assertEquals(new Byte("16"), entry.object.byteValue);
       assertEquals(new Short("120"), entry.object.shortValue);
       assertEquals(new Integer(1234), entry.object.intValue);
       assertEquals(new Float(1234.56), entry.object.floatValue);
       assertEquals(new Long(1234567), entry.object.longValue);
       assertEquals(new Double(1234567.89), entry.object.doubleValue);
       assertEquals("text value", entry.object.stringValue);
       assertEquals(TestEnum.TWO, entry.object.enumValue);
       
       IWriter xmlWriter = NanoFactory.getXMLWriter();
	   IReader xmlReader = NanoFactory.getXMLReader();
	   validateType(xmlWriter, xmlReader, entry);      
   }
   
   private void validateType(IWriter writer, IReader reader, Entry entry) throws Exception {
       String xmlStr = writer.write(entry);
       
       entry = reader.read(Entry.class, xmlStr);
       
       assertEquals(true, entry.primitive.booleanValue);
       assertEquals(16, entry.primitive.byteValue);
       assertEquals(120, entry.primitive.shortValue);
       assertEquals(1234, entry.primitive.intValue);
       assertEquals(1234.56f, entry.primitive.floatValue);
       assertEquals(1234567l, entry.primitive.longValue);
       assertEquals(1234567.89d, entry.primitive.doubleValue);

       assertEquals(Boolean.TRUE, entry.object.booleanValue);
       assertEquals(new Byte("16"), entry.object.byteValue);
       assertEquals(new Short("120"), entry.object.shortValue);
       assertEquals(new Integer(1234), entry.object.intValue);
       assertEquals(new Float(1234.56), entry.object.floatValue);
       assertEquals(new Long(1234567), entry.object.longValue);
       assertEquals(new Double(1234567.89), entry.object.doubleValue);
       assertEquals("text value", entry.object.stringValue);
       assertEquals(TestEnum.TWO, entry.object.enumValue); 
   }
   
   public void testTypeJSON() throws Exception {
	   Entry entry = getEntryFromXML();
       
       IWriter jsonWriter = NanoFactory.getJSONWriter();
       IReader jsonReader = NanoFactory.getJSONReader();
	   validateType(jsonWriter, jsonReader, entry);         
   }
}
