package com.leansoft.nano.sample;

import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.annotation.Element;

public class Books {
	 
    @Element(name = "book")
    private List<Book> books = new ArrayList<Book>();
 
    public Books() {}
 
    public Books(List<Book> books) {
        this.books = books;
    }
 
    public List<Book> getBooks() {
        return books;
    }
 
    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
