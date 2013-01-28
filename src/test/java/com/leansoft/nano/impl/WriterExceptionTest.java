package com.leansoft.nano.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import com.leansoft.nano.Format;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.exception.MappingException;
import com.leansoft.nano.exception.WriterException;

import junit.framework.TestCase;

public class WriterExceptionTest extends TestCase {
	
	private static class Zero {
		private int i = 0;
	}
	
	private static class One {
		// will thrown mapping exception since Attribute annotation can't annotate complext type.
		@Attribute
		private Zero z;
	}
	
	IWriter xmlWriter = NanoFactory.getXMLWriter();
	IWriter jsonWriter = NanoFactory.getJSONWriter();
	
	public void testMappingException() throws Exception {
		validateMappingException(xmlWriter);
		validateMappingException(jsonWriter);
	}
	
	private void validateMappingException(IWriter writer) throws Exception {
		boolean thrown = false;
		
		try {
			writer.write(new One());
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testEntryValidation() throws Exception {
		validateEntry(xmlWriter);
		validateEntry(jsonWriter);
	}
	
	private void validateEntry(IWriter writer) throws Exception {
		// source object is null
		boolean thrown = false;
		
		try {
			writer.write(null, new StringWriter());
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		// Writer is null
		thrown = false;
		
		try {
			writer.write(new One(), (Writer)null);
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		// serialize primive type directly
		thrown = false;
		
		try {
			writer.write("123");
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testUnsupportedEncoding() throws Exception {
		Format format = new Format(true, "bad_encoding");
		validateUnsupportedEncoding(NanoFactory.getXMLWriter(format));
		validateUnsupportedEncoding(NanoFactory.getJSONWriter(format));
	}
	
	private void validateUnsupportedEncoding(IWriter writer) throws Exception {
		
		boolean thrown = false;
		
		try {
			writer.write(new One(), new ByteArrayOutputStream());
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
	}

}
