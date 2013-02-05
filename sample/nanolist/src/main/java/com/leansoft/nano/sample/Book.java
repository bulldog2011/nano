package com.leansoft.nano.sample;

import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

@RootElement(name = "book")
public class Book {
	@Element
    private String name;
	@Element
    private String author;
	@Element
    private String publisher;
	@Element
    private String isbn;
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getAuthor() {
        return author;
    }
 
    public void setAuthor(String author) {
        this.author = author;
    }
 
    public String getPublisher() {
        return publisher;
    }
 
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
 
    public String getIsbn() {
        return isbn;
    }
 
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
 
    @Override
    public String toString() {
        return "Book [name=" + name + ", author=" + author + ", publisher="
                + publisher + ", isbn=" + isbn  + "]";
    }
}
