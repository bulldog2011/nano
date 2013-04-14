package com.leansoft.nano.sample;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;

/**
 * A demo show Nano list handling
 * 
 * @author bulldog
 *
 */
public class ListExample {

	public static void main(String[] args) {
		Book bookOne = new Book();
		bookOne.setAuthor("Kathy Sierra");
		bookOne.setName("SCJP");
		bookOne.setPublisher("Tata McGraw Hill");
		bookOne.setIsbn("856-545456736");
 
        Book bookTwo = new Book();
        bookTwo.setAuthor("Christian Bauer");
        bookTwo.setName("Java Persistence with Hibernate");
        bookTwo.setPublisher("Manning");
        bookTwo.setIsbn("978-3832180577");
 
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(bookOne);
        bookList.add(bookTwo);
        
        Books books = new Books();
        books.setBooks(bookList);
        
        IWriter xmlWriter = NanoFactory.getXMLWriter();
        try {
			xmlWriter.write(books, new FileOutputStream("books.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        IReader xmlReader = NanoFactory.getXMLReader();
        try {
			books = xmlReader.read(Books.class, new FileInputStream("books.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		} 

        System.out.println(books.getBooks());
        
	}

}
