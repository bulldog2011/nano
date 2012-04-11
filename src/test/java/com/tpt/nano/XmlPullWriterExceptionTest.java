package com.tpt.nano;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import com.tpt.nano.annotation.Attribute;

import junit.framework.TestCase;

public class XmlPullWriterExceptionTest extends TestCase {
	
	private static class Zero {
		private int i = 0;
	}
	
	private static class One {
		// will thrown mapping exception since Attribute annotation can't annotate complext type.
		@Attribute
		private Zero z;
	}
	
	public void testMappingException() throws Exception {
		boolean thrown = false;
		
		IWriter xmlWriter = new XmlPullWriter();
		
		try {
			xmlWriter.write(new One());
		} catch (MappingException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testEntryValidation() throws Exception {
		
		// source object is null
		boolean thrown = false;
		
		IWriter xmlWriter = new XmlPullWriter();
		
		try {
			xmlWriter.write(null, new StringWriter());
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		// Writer is null
		thrown = false;
		
		try {
			xmlWriter.write(new One(), (Writer)null);
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		// serialize primive type directly
		thrown = false;
		
		try {
			xmlWriter.write("123");
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
	
	public void testUnsupportedEncoding() throws Exception {
		Format format = new Format(true, "bad_encoding");
		IWriter xmlWriter = new XmlPullWriter(format);
		
		boolean thrown = false;
		
		try {
			xmlWriter.write(new One(), new ByteArrayOutputStream());
		} catch (WriterException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
	}

}
