package com.tpt.nano;

import java.util.List;

import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.RootElement;

import junit.framework.TestCase;

public class CustomEnumTest extends TestCase {
	
    private static final String SOURCE = 
	    "<enumBug>\n"+
	    "  <type>a</type>\n"+
	    "</enumBug>";
    
    private static final String INVALID_SOURCE = 
        "<enumBug>\n"+
        "  <type>c</type>\n"+
        "</enumBug>";
    
    private static final String LIST = 
	    "<enumVariableArgumentsBug>\n"+
	    "  <types>a</types>\n"+
	    "  <types>b</types>\n"+
	    "  <types>a</types>\n"+
	    "  <types>a</types>\n"+
	    "</enumVariableArgumentsBug>";
    
    public enum PartType {
    	A("a"),
    	B("b");
    	
    	private final String value;
    	
    	PartType(String v) {
    		value = v;
    	}
    	
    	public String value() {
    		return value;
    	}
    	
    	public static PartType fromValue(String v) {
    		for(PartType c : PartType.values()) {
    			if (c.value.equals(v)) {
    				return c;
    			}
    		}
    		throw new IllegalArgumentException(v);
    	}
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
    
    public void testEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, SOURCE);
    
    	assertEquals(bug.getType(), PartType.A);
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	String bugStr = xmlWriter.write(bug);
    	
    	EnumBug bugCopy = xmlReader.read(EnumBug.class, bugStr);
    	assertTrue(bug.getType() == bugCopy.getType());
    }
    
    public void testInvalidEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, INVALID_SOURCE);
    	
    	assertNull(bug.getType());
    }
    
    public void testVargsEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumVariableArgumentsBug bug = xmlReader.read(EnumVariableArgumentsBug.class, LIST);
    	
        assertEquals(bug.getTypes().get(0), PartType.A);
        assertEquals(bug.getTypes().get(1), PartType.B);
        assertEquals(bug.getTypes().get(2), PartType.A);
        assertEquals(bug.getTypes().get(3), PartType.A);
        
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	String bugStr = xmlWriter.write(bug);
    	EnumVariableArgumentsBug bugCopy = xmlReader.read(EnumVariableArgumentsBug.class, bugStr);
        assertEquals(bugCopy.getTypes().get(0), PartType.A);
        assertEquals(bugCopy.getTypes().get(1), PartType.B);
        assertEquals(bugCopy.getTypes().get(2), PartType.A);
        assertEquals(bugCopy.getTypes().get(3), PartType.A);
    }

}
