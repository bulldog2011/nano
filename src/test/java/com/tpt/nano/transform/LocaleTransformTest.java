package com.tpt.nano.transform;

import java.util.Locale;

import junit.framework.TestCase;

public class LocaleTransformTest extends TestCase {
	
	public void testLocale() throws Exception {
		Locale locale = Locale.UK;
		LocaleTransform transform = new LocaleTransform();
		String value = transform.write(locale);
		Locale copy = transform.read(value);
		
		assertEquals(locale, copy);

		locale = Locale.CHINA;
		value = transform.write(locale);
		copy = transform.read(value);
		
		assertEquals(locale, copy);
	}
}
