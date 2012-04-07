package com.tpt.nano.transform;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class CalendarTransformTest extends TestCase {
	
	public void testCalendar() throws Exception {
		Calendar calendar = new GregorianCalendar();
		CalendarTransform transform = new CalendarTransform();
		
		calendar.setTime(new Date());
		
		String value = transform.write(calendar);
		Calendar copy = transform.read(value);
		
		assertEquals(calendar, copy);
	}

}
