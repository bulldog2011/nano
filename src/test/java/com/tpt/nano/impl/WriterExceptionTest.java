package com.tpt.nano.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import com.tpt.nano.Format;
import com.tpt.nano.IWriter;
import com.tpt.nano.NanoFactory;
import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.exception.MappingException;
import com.tpt.nano.exception.WriterException;
import com.tpt.nano.impl.XmlPullWriter;

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
	IWriter nvWriter = NanoFactory.getNVWriter();
	
	public void testMappingException() throws Exception {
		validateMappingException(xmlWriter);
		validateMappingException(jsonWriter);
		validateMappingException(nvWriter);
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
		validateEntry(nvWriter);
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
		validateUnsupportedEncoding(NanoFactory.getNVWriter(format));
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
