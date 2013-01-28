package com.leansoft.nano.transform;

import java.sql.Time;

import com.leansoft.nano.transform.TimeTransform;

import junit.framework.TestCase;

public class TimeTransformTest extends TestCase {
	
	public void testTime() throws Exception {
		long now = System.currentTimeMillis();
		Time time = new Time(now);
		TimeTransform transform = new TimeTransform();
		String value = transform.write(time);
		Time copy = transform.read(value);
		
		assertEquals(time.toString(), copy.toString());	
	}

}
