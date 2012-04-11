package com.tpt.nano.util;

import java.util.EmptyStackException;

import junit.framework.TestCase;

public class FastStackTest extends TestCase {
	
	public void testFastStack() {
		FastStack<Integer> stack = new FastStack<Integer>(5);
		for(int i = 1; i <= 6; i++) {
			stack.push(i);
		}
		assertTrue(stack.size() == 6);
		
		assertEquals(new Integer(6), stack.peek());
		
		for(int i = 1; i <= 6; i++) {
			stack.pop();
		}
		assertTrue(stack.size() == 0);
		assertTrue(stack.isEmpty());
		
		boolean thrown = false;
		
		try {
			stack.peek();
			fail("EmptyStackException should be thrown");
		} catch(EmptyStackException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			stack.pop();
			fail("EmptyStackException should be thrown");
		} catch(EmptyStackException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
	}

}
