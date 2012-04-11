package com.tpt.nano.transform;

import junit.framework.TestCase;

public class CustomTransformTest extends TestCase {
	
	private static class Dog {
		private String name;
		
		public Dog(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public static class DogTransform implements Transformable<Dog> {

		public Dog read(String value) throws Exception {
			return new Dog(value);
		}

		public String write(Dog value) throws Exception {
			return value.toString();
		}
		
	}
	
	public void testCustomTransform() {
		assertFalse(Transformer.isTransformable(Dog.class));
		Transformer.register(Dog.class, new DogTransform());
		assertTrue(Transformer.isTransformable(Dog.class));
	}

}
