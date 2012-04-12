package com.tpt.nano.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tpt.nano.IReader;
import com.tpt.nano.IWriter;
import com.tpt.nano.NanoFactory;
import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;

import junit.framework.TestCase;

public class DateTransformTest extends TestCase {

	public static class DateExample {
		
		@Element
		private Date element;
		
		@Attribute
		private Date attribute;
		
		@Element
		private List<Date> list;
		
		public DateExample() {
			super();
		}
		
		public DateExample(Date date) {
			this.attribute = date;
			this.element = date;
			this.list = new ArrayList<Date>();
			this.list.add(date);
			this.list.add(date);
		}
	}
	
	public void testDate() throws Exception {
		Date date = new Date();
		DateTransform transform = new DateTransform();
		String value = transform.write(date);
		Date copy = transform.read(value);
		
		assertEquals(date, copy);
	}
	
	public void testLongDate() throws Exception {
		String dateString = "2012-04-10 06:57:14 GMT";
		DateTransform transform = new DateTransform();
		Date date = transform.read(dateString);
		assertEquals("10 Apr 2012 06:57:14 GMT", date.toGMTString());
	}
	
	public void testNormalDate() throws Exception {
		String dateString = "2012-04-10 GMT";
		DateTransform transform = new DateTransform();
		Date date = transform.read(dateString);
		assertEquals("10 Apr 2012 00:00:00 GMT", date.toGMTString());
	}
	
	public void testShortDate() throws Exception {
		String dateString = "2012-04-10";
		DateTransform transform = new DateTransform();
		Date date = transform.read(dateString);
		assertEquals("10 Apr 2012 00:00:00 GMT", date.toGMTString());
	}
	
	public void testDateExample() throws Exception {
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		DateExample example = new DateExample(date);
		assertEquals(date, example.attribute);
		assertEquals(date, example.element);
		assertEquals(date, example.list.get(0));
		assertEquals(date, example.list.get(1));
		
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		String text = xmlWriter.write(example);
		
		IReader xmlReader = NanoFactory.getXMLReader();
		example = xmlReader.read(DateExample.class, text);
		
		assertEquals(date, example.attribute);
		assertEquals(date, example.element);
		assertEquals(date, example.list.get(0));
		assertEquals(date, example.list.get(1));
	}
	
}
