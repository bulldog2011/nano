package com.tpt.nano;

import com.tpt.nano.annotation.XmlAttribute;
import com.tpt.nano.annotation.XmlElement;
import com.tpt.nano.annotation.XmlRootElement;

import junit.framework.TestCase;

public class RootElementTest extends TestCase {
	private static final String ROOT_EXAMPLE =
	   "<rootExample version='1'>\n"+
	   "  <text>Some text example</text>\n"+
	   "</rootExample>";
   
	private static final String EXTENDED_ROOT_EXAMPLE =
       "<extendedRootExample version='1'>\n"+
       "  <text>Some text example</text>\n"+
       "</extendedRootExample>";   
   
	private static final String EXTENDED_OVERRIDDEN_ROOT_EXAMPLE =
       "<extendedOverriddenRootExample version='1'>\n"+
       "  <text>Some text example</text>\n"+
       "</extendedOverriddenRootExample>";
   
	private static final String EXTENDED_EXPLICITLY_OVERRIDDEN_ROOT_EXAMPLE =
       "<explicitOverride version='1'>\n"+
       "  <text>Some text example</text>\n"+
       "</explicitOverride>";
	
	@XmlRootElement
	private static class RootExample {
		@XmlAttribute
		private int version;
		@XmlElement
		private String text;
	}
	
	private static class ExtendedRootExample extends RootExample {
	}
	
	@XmlRootElement
	private static class ExtendedOverriddenRootExample extends ExtendedRootExample {
	}
	
	@XmlRootElement(name="explicitOverride")
	private static class ExtendedExplicitlyOverriddenRootExample extends ExtendedRootExample {
		
	}
	
	public void testRoot() throws Exception {
		IReader xmlReader = NanoFactory.getXMLReader();
		RootExample example = xmlReader.read(RootExample.class, ROOT_EXAMPLE);
		
		assertEquals(1, example.version);
		assertEquals("Some text example", example.text);
		
		example = xmlReader.read(ExtendedRootExample.class, EXTENDED_ROOT_EXAMPLE);
		assertEquals(1, example.version);
		assertEquals("Some text example", example.text);
		
		example = xmlReader.read(ExtendedOverriddenRootExample.class, EXTENDED_OVERRIDDEN_ROOT_EXAMPLE);
		assertEquals(1, example.version);
		assertEquals("Some text example", example.text);
		
		example = xmlReader.read(ExtendedExplicitlyOverriddenRootExample.class, EXTENDED_EXPLICITLY_OVERRIDDEN_ROOT_EXAMPLE);
		assertEquals(1, example.version);
		assertEquals("Some text example", example.text);
	}
}
