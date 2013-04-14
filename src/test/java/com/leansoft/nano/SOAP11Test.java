package com.leansoft.nano;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import com.leansoft.nano.impl.SOAPReader;
import com.leansoft.nano.impl.SOAPWriter;
import com.leansoft.nano.soap11.Fault;

import junit.framework.TestCase;

public class SOAP11Test extends TestCase {
	
	public void testSOAP11() throws Exception {
		com.leansoft.nano.soap11.Envelope envelope = new com.leansoft.nano.soap11.Envelope();
		com.leansoft.nano.soap11.Header header = new com.leansoft.nano.soap11.Header();
		envelope.header = header;
		header.any = new ArrayList<Object>();
		Bulldog dog = getBulldog();
		header.any.add(dog);
		
		com.leansoft.nano.soap11.Body body = new com.leansoft.nano.soap11.Body();
		envelope.body = body;
		body.any = new ArrayList<Object>();
		body.any.add(dog);
		
		SOAPWriter soapWriter = new SOAPWriter();
		
		String soap11String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap11String);
		
		SOAPReader soapReader = new SOAPReader();
		envelope = soapReader.read(com.leansoft.nano.soap11.Envelope.class, Bulldog.class, new ByteArrayInputStream(soap11String.getBytes()));
		
		assertDogEquals(dog, (Bulldog)envelope.body.any.get(0));
		
		soap11String = soapWriter.write(envelope);
		System.out.println("write result :");
		System.out.println(soap11String);
	}
	
	public void testSOAP11Fault() throws Exception {
		com.leansoft.nano.soap11.Envelope envelope = new com.leansoft.nano.soap11.Envelope();
		com.leansoft.nano.soap11.Header header = new com.leansoft.nano.soap11.Header();
		envelope.header = header;
		header.any = new ArrayList<Object>();
		Bulldog dog = getBulldog();
		header.any.add(dog);
		
		com.leansoft.nano.soap11.Body body = new com.leansoft.nano.soap11.Body();
		envelope.body = body;
		body.any = new ArrayList<Object>();
		com.leansoft.nano.soap11.Fault fault = new com.leansoft.nano.soap11.Fault();
		fault.faultstring = "test";
		fault.faultcode = new QName(null, "2000", "nano");
		body.any.add(fault);
		
		SOAPWriter soapWriter = new SOAPWriter();
		
		String soap11String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap11String);
		
		SOAPReader soapReader = new SOAPReader();
		envelope = soapReader.read(com.leansoft.nano.soap11.Envelope.class, Bulldog.class, new ByteArrayInputStream(soap11String.getBytes()));
		
		assertNotNull(envelope.body);
		assertTrue(envelope.body.any.size() == 1);
		fault = (Fault) envelope.body.any.get(0);
		assertEquals("test", fault.faultstring);
		assertEquals("nano", fault.faultcode.getPrefix());
		assertEquals("2000", fault.faultcode.getLocalPart());
		
		soap11String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap11String);
	}

	public static Bulldog getBulldog() {
		Bulldog dog = new Bulldog();
		
		dog.name = "tomy";
		dog.age = 3;
		dog.color = "WHITE";
		dog.desc = "my favorate dog";
		
		Bulldog dog1 = new Bulldog();
		dog1.name = "jacky";
		dog1.age = 1;
		dog1.color = "YELLOW";
		dog1.desc = "my baby dog";
		
		Bulldog dog2 = new Bulldog();
		dog2.name = "andy";
		dog2.age = 1;
		dog2.color = "BLACE";
		dog2.desc = "tomy's child dog";
		
		dog.children = new ArrayList<Bulldog>();
		dog.children.add(dog1);
		dog.children.add(dog2);
		
		return dog;
	}
	
	public static void assertDogEquals(Bulldog dog1, Bulldog dog2) {
		assertEquals(dog1.name, dog2.name);
		assertEquals(dog1.age, dog2.age);
		assertEquals(dog1.color, dog2.color);
		assertEquals(dog1.desc, dog2.desc);
		if (dog1.children != null && dog1.children.size() > 0) {
			assertTrue(dog1.children.size() == 2);
			assertTrue(dog2.children.size() == 2);
			for(int i = 0; i < 2; i++) {
				assertDogEquals(dog1.children.get(i), dog2.children.get(i));
			}
		}
	}
}
