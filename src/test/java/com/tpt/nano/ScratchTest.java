package com.tpt.nano;

import java.util.List;
import com.tpt.nano.util.TypeReflector;

import junit.framework.TestCase;

public class ScratchTest extends TestCase {
	
	public void testTypeReflector() {
		assertTrue(TypeReflector.isList(List.class));
	}

}
