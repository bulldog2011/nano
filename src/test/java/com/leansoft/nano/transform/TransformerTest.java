package com.leansoft.nano.transform;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Currency;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import com.leansoft.nano.custom.types.Duration;
import com.leansoft.nano.transform.Transformer;

import junit.framework.TestCase;

public class TransformerTest extends TestCase {
	
	public void testInteger() throws Exception {
		Object value = Transformer.read("1", Integer.class);
		String text = Transformer.write(value, Integer.class);
		
		assertEquals(value, new Integer(1));
		assertEquals(text, "1");
	}
	
	public void testString() throws Exception {     
		Object value = Transformer.read("some text", String.class);      
        String text = Transformer.write(value, String.class);
      
        assertEquals("some text", value);
        assertEquals("some text", text);
    }
	
	public void testCharacter() throws Exception {
		Object value = Transformer.read("c", Character.class);      
		String text = Transformer.write(value, Character.class);      
		      
		assertEquals(value, new Character('c'));
		assertEquals(text, "c");
	}
	
    public void testInvalidCharacter() throws Exception {
    	boolean success = false;
	      
	    try {
	    	Transformer.read("too long", Character.class);
	    }catch(IllegalArgumentException e) {
	         //e.printStackTrace();
	         success = true;
	    }
	    assertTrue(success);
	}
    
    public void testFloat() throws Exception {
        Object value = Transformer.read("1.12", Float.class);      
        String text = Transformer.write(value, Float.class);
        
        assertEquals(value, new Float(1.12));
        assertEquals(text, "1.12");
    }
    
    public void testDouble() throws Exception {     
        Object value = Transformer.read("12.33", Double.class);
        String text = Transformer.write(value, Double.class);      
        
        assertEquals(value, new Double(12.33));
        assertEquals(text, "12.33");
    }
    
    public void testBoolean() throws Exception {
        Object value = Transformer.read("true", Boolean.class);
        String text = Transformer.write(value, Boolean.class);
        
        assertEquals(value, Boolean.TRUE);
        assertEquals(text, "true");
    }
    
    public void testLong() throws Exception {
        Object value = Transformer.read("1234567", Long.class);
        String text = Transformer.write(value, Long.class);
        
        assertEquals(value, new Long(1234567));
        assertEquals(text, "1234567");
    }
    
    public void testShort() throws Exception {
        Object value = Transformer.read("12", Short.class);
        String text = Transformer.write(value, Short.class);
        
        assertEquals(value, new Short((short)12));
        assertEquals(text, "12");
    }
    
    public void testQName() throws Exception {
    	QName qname = (QName) Transformer.read("pre:test", QName.class);
    	assertEquals("pre", qname.getPrefix());
    	assertEquals("test", qname.getLocalPart());
    	
    	String text = Transformer.write(qname, QName.class);
    	
    	assertEquals(text, "pre:test");
    }

    public void testTransformable() throws Exception {
    	assertTrue(Transformer.isTransformable(byte[].class));
    	assertTrue(Transformer.isTransformable(char.class));
    	assertTrue(Transformer.isTransformable(BigDecimal.class));
    	assertTrue(Transformer.isTransformable(BigInteger.class));
    	assertTrue(Transformer.isTransformable(Currency.class));
    	assertTrue(Transformer.isTransformable(TimeZone.class));
    	assertTrue(Transformer.isTransformable(Duration.class));
    	assertTrue(Transformer.isTransformable(URL.class));
    	assertTrue(Transformer.isTransformable(Time.class));
    	assertTrue(Transformer.isTransformable(QName.class));
    	assertFalse(Transformer.isTransformable(ArrayList.class));
    }
    
}
