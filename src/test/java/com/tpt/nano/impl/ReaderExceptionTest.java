package com.tpt.nano.impl;

import java.io.Reader;

import com.tpt.nano.IReader;
import com.tpt.nano.NanoFactory;
import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;
import com.tpt.nano.exception.MappingException;
import com.tpt.nano.exception.ReaderException;

import junit.framework.TestCase;

public class ReaderExceptionTest extends TestCase {
	
	private static final String SOURCE_XML = 		   
			   "<?xml version=\"1.0\"?>\n"+
			   "<book>\n"+
			   "   <title>test title</title>\n"+
			   "   <description>test description</description>\n"+
			   "</book>";
	
	private static final String SOURCE_JSON = 		   
			"{\"book\":{\"title\":\"test title\",\"description\":\"test description\"}}";
	
	private static final String SOURCE_NV = 
			"book.title=test+title&book.description=test+description";

	private static final String SOURCE_ROOT_MISMATCH = 		   
			   "<?xml version=\"1.0\"?>\n"+
			   "<mismatch>\n"+
			   "   <title>test title</title>\n"+
			   "   <description>test description</description>\n"+
			   "</mismatch>";
	
	private static final String SOURCE_REDUNDANT_ELEMENT_XML = 
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
	
	private static final String SOURCE_REDUNDANT_ELEMENT_JSON = "{\"book\":{\"title\":\"test title\",\"publisher\":{\"name\":\"nanopub\", \"address\":\"pop square\"},\"description\":\"test description\"}}";
	
//	private static final String SOURCE_REDUNDANT_ELEMENT_NV = "book.author=bulldog&book.title=test+title&book.description=test+description";
	private static final String SOURCE_REDUNDANT_ELEMENT_NV = "book.title=test+title&book.publisher.name=nanopub&book.publisher.address=pop+square&book.description=test+description&book.author=bulldog";
	
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
		// will thrown mapping exception since Attribute annotation can't annotate complex type.
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
	private IReader jsonReader = NanoFactory.getJSONReader();
	private IReader nvReader = NanoFactory.getNVReader();
	
	public void testRootElementMismatch() throws Exception {
		validateRootElementMismatch(xmlReader);
		validateRootElementMismatch(jsonReader);
		validateRootElementMismatch(nvReader);
	}
	
	private void validateRootElementMismatch(IReader reader) throws Exception {
		boolean thrown = false;
		
		try {
			reader.read(Book.class, SOURCE_ROOT_MISMATCH);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	
	public void testRedundantElementsXML() throws Exception {
		Book book = xmlReader.read(Book.class, SOURCE_REDUNDANT_ELEMENT_XML);
		assertEquals("test title", book.title);
		assertEquals("test description", book.desc);
	}
	
	public void testRedundantElementsJSON() throws Exception {
		Book book = jsonReader.read(Book.class, SOURCE_REDUNDANT_ELEMENT_JSON);
		assertEquals("test title", book.title);
		assertEquals("test description", book.desc);
	}
	
	public void testRedundantElementsNV() throws Exception {
		Book book = nvReader.read(Book.class, SOURCE_REDUNDANT_ELEMENT_NV);
		assertEquals("test title", book.title);
		assertEquals("test description", book.desc);
	}
	
	public void testMappingExceptionXML() throws Exception {
		boolean thrown = false;
		
		try {
			xmlReader.read(One.class, "<one z=\"0\"></one>");
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testMappingExceptionJSON() throws Exception {
		boolean thrown = false;
		
		try {
			jsonReader.read(One.class, "{\"one\":{\"@z\":0}}");
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testMappingExceptionNV() throws Exception {
		boolean thrown = false;
		
		try {
			nvReader.read(One.class, "one.@z=0");
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testEntryValidation() throws Exception {
		validateEntry(xmlReader, SOURCE_XML);
		validateEntry(jsonReader, SOURCE_JSON);
		validateEntry(nvReader, SOURCE_NV);
	}
	
	private void validateEntry(IReader reader, String source) throws Exception {
		boolean thrown = false;
		
		try {
			reader.read(Book.class, (Reader)null);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			reader.read(null, source);
		} catch (ReaderException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			reader.read(int.class, source);
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
