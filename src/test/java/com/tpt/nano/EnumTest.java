package com.tpt.nano;

import java.util.List;

import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.RootElement;

import junit.framework.TestCase;

public class EnumTest extends TestCase {
	
    private static final String SOURCE = 
	    "<enumBug>\n"+
	    "  <type>A</type>\n"+
	    "</enumBug>";
        
    private static final String INVALID_SOURCE = 
        "<enumBug>\n"+
        "  <type>C</type>\n"+
        "</enumBug>";
        
    private static final String LIST = 
	    "<enumVariableArgumentsBug>\n"+
	    "  <types>A</types>\n"+
	    "  <types>B</types>\n"+
	    "  <types>A</types>\n"+
	    "  <types>A</types>\n"+
	    "</enumVariableArgumentsBug>";
    
    public enum PartType {
        A,
        B;
    }
    
    @RootElement
    public static class EnumVariableArgumentsBug {
    	
    	@Element
    	private List<PartType> types;
    	
    	public List<PartType> getTypes() {
    		return types;
    	}
    }
    
    @RootElement
    public static class EnumBug {
    	
    	@Element
    	private PartType type;
    	
    	public PartType getType() {
    		return type;
    	}
    }
    
    public void testEnumXML() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, SOURCE);
    
    	assertEquals(PartType.A, bug.getType());
    }
    
    public void testEnumJSON() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, SOURCE);
    
    	assertEquals(PartType.A, bug.getType());
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	String text = jsonWriter.write(bug);
    	
    	IReader jsonReader = NanoFactory.getJSONReader();
    	EnumBug copy = jsonReader.read(EnumBug.class, text);
    
    	assertEquals(PartType.A, copy.getType());
    	
    }
    
    public void testInvalidEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, INVALID_SOURCE);
    	
    	assertNull(bug.getType());
    }
    
    public void testVargsEnumXML() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumVariableArgumentsBug bug = xmlReader.read(EnumVariableArgumentsBug.class, LIST);
    	
        assertEquals(PartType.A, bug.getTypes().get(0));
        assertEquals(PartType.B, bug.getTypes().get(1));
        assertEquals(PartType.A, bug.getTypes().get(2));
        assertEquals(PartType.A, bug.getTypes().get(3));
    }
    
    public void testVargsEnumJSON() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumVariableArgumentsBug bug = xmlReader.read(EnumVariableArgumentsBug.class, LIST);
    	
        assertEquals(PartType.A, bug.getTypes().get(0));
        assertEquals(PartType.B, bug.getTypes().get(1));
        assertEquals(PartType.A, bug.getTypes().get(2));
        assertEquals(PartType.A, bug.getTypes().get(3));
        
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	String text = jsonWriter.write(bug);
    	
    	IReader jsonReader = NanoFactory.getJSONReader();
    	EnumVariableArgumentsBug copy = jsonReader.read(EnumVariableArgumentsBug.class, text);
    	
        assertEquals(PartType.A, copy.getTypes().get(0));
        assertEquals(PartType.B, copy.getTypes().get(1));
        assertEquals(PartType.A, copy.getTypes().get(2));
        assertEquals(PartType.A, copy.getTypes().get(3));
    }

}
