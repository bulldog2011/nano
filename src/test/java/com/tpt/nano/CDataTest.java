package com.tpt.nano;
import com.tpt.nano.annotation.Element;

import junit.framework.TestCase;


public class CDataTest extends TestCase {
	
	private static class Book {
		@Element
		private String title;
		@Element(data=true)
		private String description;
	}
	
	public void testCData() throws Exception {
		Book book = new Book();
		book.title = "a test book";
		book.description = "a test description";
		
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		String bookStr = xmlWriter.write(book);
		System.out.println(bookStr);
		
		IReader xmlReader = NanoFactory.getXMLReader();
		Book copy = xmlReader.read(Book.class, bookStr);
		
		assertEquals(copy.title, book.title);
		assertEquals(copy.description.trim(), book.description);
	}

}
