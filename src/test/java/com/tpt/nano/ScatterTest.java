package com.tpt.nano;

import java.util.List;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.RootElement;
import com.tpt.nano.annotation.Value;

import junit.framework.TestCase;

public class ScatterTest extends TestCase {
    private static final String INLINE_LIST =
	   "<test version='ONE'>\n"+
	   "   <list name='a' version='ONE'>Example 1</list>\r\n"+
	   "   <message>Some example message</message>\r\n"+
	   "   <list name='b' version='TWO'>Example 2</list>\r\n"+
	   "   <numbers>1.0</numbers>\n" +
	   "   <numbers>2.0</numbers>\n"+
	   "   <list name='c' version='THREE'>Example 3</list>\r\n"+
	   "   <numbers>3.0</numbers>\n"+
	   "</test>";
   
    private static final String INLINE_PRIMITIVE_LIST =
	   "<test version='ONE'>\n"+
	   "   <list>Example 1</list>\r\n"+
	   "   <message>Some example message</message>\r\n"+
	   "   <list>Example 2</list>\r\n"+
	   "   <list>Example 3</list>\r\n"+
	   "</test>";
   
    private static final String INLINE_NAMED_LIST =
	   "<test version='ONE'>\n"+
	   "   <include name='1' file='1.txt'/>\r\n"+
	   "   <exclude name='2' file='2.txt'/>\r\n"+
	   "   <exclude name='3' file='3.txt'/>\r\n"+  
	   "   <include name='4' file='4.txt'/>\r\n"+
	   "   <exclude name='5' file='5.txt'/>\r\n"+
	   "</test>";  
    
    @RootElement(name="test")
    private static class InlineTextList {
    	@Element
    	private String message;
    	
    	@Element
    	private List<Double> numbers;
    	
    	@Element
    	private List<TextEntry> list;
    	
    	@Attribute
    	private Version version;
    	
    	private List<Double> getNumbers() {
    		return numbers;
    	}
    	
    	public TextEntry get(int index) {
    		return list.get(index);
    	}
    }
    
    @RootElement(name="test")
    private static class InlinePrimitiveList {
    	@Element
    	private String message;
    	
    	@Element
    	private List<String> list;
    	
    	@Attribute
    	private Version version;
    	
    	public String get(int index) {
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
    
    private static enum Version {
    	ONE,
    	TWO,
    	THREE;
    }
    
    public void testListXML() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	InlineTextList list = xmlReader.read(InlineTextList.class, INLINE_LIST);
    	
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);
        assertEquals(Version.ONE, list.get(0).version);
        assertEquals("a", list.get(0).name);
        assertEquals("Example 1", list.get(0).text);
        assertEquals(Version.TWO, list.get(1).version);
        assertEquals("b", list.get(1).name);
        assertEquals("Example 2", list.get(1).text);
        assertEquals(Version.THREE, list.get(2).version);
        assertEquals("c", list.get(2).name);
        assertEquals("Example 3", list.get(2).text);
        assertTrue(list.getNumbers().contains(1.0));
        assertTrue(list.getNumbers().contains(2.0));
        assertTrue(list.getNumbers().contains(3.0));
        
        IWriter xmlWriter = NanoFactory.getXMLWriter();
        String str = xmlWriter.write(list);
        list = xmlReader.read(InlineTextList.class, str);
        
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);
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
    	InlineTextList list = xmlReader.read(InlineTextList.class, INLINE_LIST);
    	
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);
        assertEquals(Version.ONE, list.get(0).version);
        assertEquals("a", list.get(0).name);
        assertEquals("Example 1", list.get(0).text);
        assertEquals(Version.TWO, list.get(1).version);
        assertEquals("b", list.get(1).name);
        assertEquals("Example 2", list.get(1).text);
        assertEquals(Version.THREE, list.get(2).version);
        assertEquals("c", list.get(2).name);
        assertEquals("Example 3", list.get(2).text);
        assertTrue(list.getNumbers().contains(1.0));
        assertTrue(list.getNumbers().contains(2.0));
        assertTrue(list.getNumbers().contains(3.0));
        
        IWriter jsonWriter = NanoFactory.getJSONWriter();
        IReader jsonReader = NanoFactory.getJSONReader();
        String str = jsonWriter.write(list);
        list = jsonReader.read(InlineTextList.class, str);
        
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);
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
    
    public void testPrimitiveListXML() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	InlinePrimitiveList list = xmlReader.read(InlinePrimitiveList.class, INLINE_PRIMITIVE_LIST);
    	
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);

        assertEquals("Example 1", list.get(0));
        assertEquals("Example 2", list.get(1));
        assertEquals("Example 3", list.get(2));
        
        IWriter xmlWriter = NanoFactory.getXMLWriter();
        String str = xmlWriter.write(list);
        list = xmlReader.read(InlinePrimitiveList.class, str);
        
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);

        assertEquals("Example 1", list.get(0));
        assertEquals("Example 2", list.get(1));
        assertEquals("Example 3", list.get(2));
    }

    public void testPrimitiveListJSON() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	InlinePrimitiveList list = xmlReader.read(InlinePrimitiveList.class, INLINE_PRIMITIVE_LIST);
    	
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);

        assertEquals("Example 1", list.get(0));
        assertEquals("Example 2", list.get(1));
        assertEquals("Example 3", list.get(2));
        
        IWriter jsonWriter = NanoFactory.getJSONWriter();
        IReader jsonReader = NanoFactory.getJSONReader();
        String str = jsonWriter.write(list);
        list = jsonReader.read(InlinePrimitiveList.class, str);
        
        assertEquals(Version.ONE, list.version);
        assertEquals("Some example message", list.message);

        assertEquals("Example 1", list.get(0));
        assertEquals("Example 2", list.get(1));
        assertEquals("Example 3", list.get(2));
    }
}
