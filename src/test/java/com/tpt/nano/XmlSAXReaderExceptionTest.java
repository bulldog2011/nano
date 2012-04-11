package com.tpt.nano;

import java.io.Reader;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;
import junit.framework.TestCase;

public class XmlSAXReaderExceptionTest extends TestCase {
	
	private static final String SOURCE = 		   
			   "<?xml version=\"1.0\"?>\n"+
			   "<book>\n"+
			   "   <title>test title</title>\n"+
			   "   <description>test description</description>\n"+
			   "</book>";

	private static final String SOURCE_ROOT_MISMATCH = 		   
			   "<?xml version=\"1.0\"?>\n"+
			   "<mismatch>\n"+
			   "   <title>test title</title>\n"+
			   "   <description>test description</description>\n"+
			   "</mismatch>";
	
	private static final String SOURCE_REDUNDANT_ELEMENT = 
			   "<?xml version=\"1.0\"?>\n"+
			   "<book>\n"+
			   "   <title>test title</title>\n"+
			   "   <publisher>\n" +
			   "      <name>nanopub</name>" +
			   "      <address>people square</address>\n" +
			   "   </publisher>\n" + 
			   "   <description>test description</description>\n"+
			   "   <author>bulldog</author>" +
			   "</book>";
	
	//@RootElement(name="book")
	private static class Book {
		@Element
		private String title;
		@Element(name="description")
		private String desc;
	}
	
	private static class Zero {
		private int i = 0;
	}

	private static class One {
		// will thrown mapping exception since Attribute annotation can't annotate complext type.
		@Attribute
		private Zero z;
	}
	
	// should thrown exception since there is no no-arg constructor
	private static class Two {
		public Two(int i) {
			this.i = i;
		}
		@Element
		private int i = 2;
	}
	
	
	private IReader xmlReader = NanoFactory.getXMLReader();
	
	public void testRootElementMismatch() throws Exception {
		boolean thrown = false;
		
		try {
			xmlReader.read(Book.class, SOURCE_ROOT_MISMATCH);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	
	public void testRedundantElements() throws Exception {
		Book book = xmlReader.read(Book.class, SOURCE_REDUNDANT_ELEMENT);
		assertEquals("test title", book.title);
		assertEquals("test description", book.desc);
	}
	
	public void testMappingException() throws Exception {
		boolean thrown = false;
		
		try {
			xmlReader.read(One.class, "<one z=\"0\"></one>");
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testEntryValidation() throws Exception {
		boolean thrown = false;
		
		try {
			xmlReader.read(Book.class, (Reader)null);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			xmlReader.read(null, SOURCE);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			xmlReader.read(int.class, SOURCE);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testMissingNoargConstructor() throws Exception {
		boolean thrown = false;
		
		try {
			xmlReader.read(Two.class, "<two><i>2</i></two>");
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
}
