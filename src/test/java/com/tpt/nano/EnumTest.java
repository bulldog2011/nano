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
    
    private EnumBug getEnumBugFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, SOURCE);
    	return bug;
    }
    
    public void testEnumJSON() throws Exception {
    	EnumBug bug = getEnumBugFromXMLSource();
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	IReader jsonReader = NanoFactory.getJSONReader();
    	validateEnumBug(jsonWriter, jsonReader, bug);
    }
    
    public void testEnumNV() throws Exception {
    	EnumBug bug = getEnumBugFromXMLSource();
    	
    	IWriter nvWriter = NanoFactory.getNVWriter();
    	IReader nvReader = NanoFactory.getNVReader();
    	validateEnumBug(nvWriter, nvReader, bug);
    }
    
    private void validateEnumBug(IWriter writer, IReader reader, EnumBug bug) throws Exception {
    	String text = writer.write(bug);
    	
    	EnumBug copy = reader.read(EnumBug.class, text);
    
    	assertEquals(PartType.A, copy.getType());
    }
    
    public void testInvalidEnum() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumBug bug = xmlReader.read(EnumBug.class, INVALID_SOURCE);
    	
    	assertNull(bug.getType());
    }
    
    private EnumVariableArgumentsBug getEnumVargsEnumFromXMLSource() throws Exception {
    	IReader xmlReader = NanoFactory.getXMLReader();
    	EnumVariableArgumentsBug bug = xmlReader.read(EnumVariableArgumentsBug.class, LIST);
    	return bug;
    }
    
    public void testVargsEnumXML() throws Exception {
    	EnumVariableArgumentsBug bug = this.getEnumVargsEnumFromXMLSource();
    	
        assertEquals(PartType.A, bug.getTypes().get(0));
        assertEquals(PartType.B, bug.getTypes().get(1));
        assertEquals(PartType.A, bug.getTypes().get(2));
        assertEquals(PartType.A, bug.getTypes().get(3));
        
    	IWriter xmlWriter = NanoFactory.getXMLWriter();
    	IReader xmlReader = NanoFactory.getXMLReader();
    	validateVargsEnum(xmlWriter, xmlReader, bug);
    }
    
    private void validateVargsEnum(IWriter writer, IReader reader, EnumVariableArgumentsBug bug) throws Exception {
    	String text = writer.write(bug);
    	
    	EnumVariableArgumentsBug copy = reader.read(EnumVariableArgumentsBug.class, text);
    	
        assertEquals(PartType.A, copy.getTypes().get(0));
        assertEquals(PartType.B, copy.getTypes().get(1));
        assertEquals(PartType.A, copy.getTypes().get(2));
        assertEquals(PartType.A, copy.getTypes().get(3));
    }
    
    public void testVargsEnumJSON() throws Exception {
    	EnumVariableArgumentsBug bug = this.getEnumVargsEnumFromXMLSource();
    	
    	IWriter jsonWriter = NanoFactory.getJSONWriter();
    	IReader jsonReader = NanoFactory.getJSONReader();
    	validateVargsEnum(jsonWriter, jsonReader, bug);
    }
    
    public void testVargsEnumNV() throws Exception {
    	EnumVariableArgumentsBug bug = this.getEnumVargsEnumFromXMLSource();
    	
    	IWriter nvWriter = NanoFactory.getNVWriter();
    	IReader nvReader = NanoFactory.getNVReader();
    	validateVargsEnum(nvWriter, nvReader, bug);
    }

}
