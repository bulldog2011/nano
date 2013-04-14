package com.leansoft.nano.transform;

import javax.xml.namespace.QName;

import com.leansoft.nano.util.StringUtil;

import junit.framework.TestCase;

public class QNameTransformTest extends TestCase {

	public void testQName() throws Exception {
		QName qname = new QName(null, "testName", "testPrefix");
		String stringQName = new QNameTransform().write(qname);
		assertEquals("testPrefix:testName", stringQName);
		qname = new QNameTransform().read(stringQName);
		assertEquals("testName", qname.getLocalPart());
		assertEquals("testPrefix", qname.getPrefix());
		
		qname = new QName("testName");
		stringQName = new QNameTransform().write(qname);
		assertEquals("testName", stringQName);
		qname = new QNameTransform().read(stringQName);
		assertEquals("testName", qname.getLocalPart());
		assertTrue(StringUtil.isEmpty(qname.getPrefix()));
		
		qname = new QName("");
		stringQName = new QNameTransform().write(qname);
		assertNull(stringQName);
		
		qname = new QName(null, "", "");
		stringQName = new QNameTransform().write(qname);
		assertNull(stringQName);
		
		
		stringQName = ":";
		qname = new QNameTransform().read(stringQName);
		assertNull(qname);
		
		stringQName = "";
		qname = new QNameTransform().read(stringQName);
		assertTrue(StringUtil.isEmpty(qname.getLocalPart()));
		assertTrue(StringUtil.isEmpty(qname.getPrefix()));
		
		stringQName = ":local";
		qname = new QNameTransform().read(stringQName);
		assertEquals("local", qname.getLocalPart());
		assertTrue(StringUtil.isEmpty(qname.getPrefix()));
		
		stringQName = "prefix:";
		qname = new QNameTransform().read(stringQName);
		assertTrue(StringUtil.isEmpty(qname.getPrefix()));
		assertEquals("prefix", qname.getLocalPart());
	}
		
	
}
