package com.leansoft.nano;

import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

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
    
    private EnumBug getEnumBugFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, SOURCE);
    	return bug;
    }
    
    public void testEnumXML() throws Exception {
    	EnumBug bug = this.getEnumBugFromXMLSource();
    
    	assertEquals(bug.getType(), PartType.A);
    	
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	IReader xmlReader = NanoFactory.getXMLReader();

    	validateEnum(xmlWriter, xmlReader, bug);
    }
    
    private void validateEnum(IWriter writer, IReader reader, EnumBug bug) throws Exception {
    	String bugStr = writer.write(bug);
    	
    	EnumBug bugCopy = reader.read(EnumBug.class, bugStr);
    	assertTrue(bug.getType() == bugCopy.getType());
    }
    
    public void testEnumJSON() throws Exception {
    	EnumBug bug = this.getEnumBugFromXMLSource();
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	IReader jsonReader = NanoFactory.getJSONReader();
    	
    	validateEnum(jsonWriter, jsonReader, bug);
    }
    
    public void testInvalidEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, INVALID_SOURCE);
    	
    	assertNull(bug.getType());
    }
    
    private EnumVariableArgumentsBug getVargsEnumFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumVariableArgumentsBug bug = xmlReader.read(EnumVariableArgumentsBug.class, LIST);
    	return bug;
    }
    
    public void testVargsEnumXML() throws Exception {
    	EnumVariableArgumentsBug bug = getVargsEnumFromXMLSource();
    	
        assertEquals(bug.getTypes().get(0), PartType.A);
        assertEquals(bug.getTypes().get(1), PartType.B);
        assertEquals(bug.getTypes().get(2), PartType.A);
        assertEquals(bug.getTypes().get(3), PartType.A);
        
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	IReader xmlReader = NanoFactory.getXMLReader();
    	
    	validateVargsEnum(xmlWriter, xmlReader, bug);
    }
    
    private void validateVargsEnum(IWriter writer, IReader reader, EnumVariableArgumentsBug bug) throws Exception {
    	String bugStr = writer.write(bug);
    	EnumVariableArgumentsBug bugCopy = reader.read(EnumVariableArgumentsBug.class, bugStr);
        assertEquals(bugCopy.getTypes().get(0), PartType.A);
        assertEquals(bugCopy.getTypes().get(1), PartType.B);
        assertEquals(bugCopy.getTypes().get(2), PartType.A);
        assertEquals(bugCopy.getTypes().get(3), PartType.A);
    }
    
    public void testVargsEnumJSON() throws Exception {
    	EnumVariableArgumentsBug bug = getVargsEnumFromXMLSource();
        
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	IReader jsonReader = NanoFactory.getJSONReader();
    	
    	validateVargsEnum(jsonWriter, jsonReader, bug);
    }

}
