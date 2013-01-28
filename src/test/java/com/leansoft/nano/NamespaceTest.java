package com.leansoft.nano;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

import junit.framework.TestCase;

public class NamespaceTest extends TestCase {
	
	@RootElement(namespace="namespace1")
	private static class Aaa {
		@Element
		public Bbb bbb;
	}
	
	//@XmlRootElement(namespace="namespace2")
	private static class Bbb {
		@Element
		public Aaa aaa;
	}
	
	public void testNamespace() throws Exception {
		Aaa parent = new Aaa();
		Bbb child = new Bbb();
		parent.bbb = child;
		Aaa grandChild = new Aaa();
		child.aaa = grandChild;
		grandChild.bbb = new Bbb();
		
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		String str1 = xmlWriter.write(parent);
		System.out.println(str1);
		
		IReader xmlReader = NanoFactory.getXMLReader();
		Aaa target = xmlReader.read(Aaa.class, str1);
		String str2 = xmlWriter.write(target);
		
		assertEquals(str2, str1);
	}

}
