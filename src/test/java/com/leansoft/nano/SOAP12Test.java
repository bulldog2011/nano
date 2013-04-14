package com.leansoft.nano;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import com.leansoft.nano.impl.SOAPReader;
import com.leansoft.nano.impl.SOAPWriter;

import com.leansoft.nano.soap12.*;

import junit.framework.TestCase;

public class SOAP12Test extends TestCase {
	
	public void testSOAP12() throws Exception {
		com.leansoft.nano.soap12.Envelope envelope = new com.leansoft.nano.soap12.Envelope();
		com.leansoft.nano.soap12.Header header = new com.leansoft.nano.soap12.Header();
		envelope.header = header;
		header.any = new ArrayList<Object>();
		Bulldog dog = SOAP11Test.getBulldog();
		header.any.add(dog);
		
		com.leansoft.nano.soap12.Body body = new com.leansoft.nano.soap12.Body();
		envelope.body = body;
		body.any = new ArrayList<Object>();
		body.any.add(dog);
		
		SOAPWriter soapWriter = new SOAPWriter();
		
		String soap12String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap12String);
		
		SOAPReader soapReader = new SOAPReader();
		envelope = soapReader.read(com.leansoft.nano.soap12.Envelope.class, Bulldog.class, new ByteArrayInputStream(soap12String.getBytes()));
		
		SOAP11Test.assertDogEquals(dog, (Bulldog)envelope.body.any.get(0));
		
		soap12String = soapWriter.write(envelope);
		System.out.println("write result :");
		System.out.println(soap12String);
	}
	
	public void testSOAP12Fault() throws Exception {
		Envelope envelope = new Envelope();
		Header header = new Header();
		envelope.header = header;
		header.any = new ArrayList<Object>();
		Bulldog dog = SOAP11Test.getBulldog();
		header.any.add(dog);
		
		Body body = new Body();
		envelope.body = body;
		body.any = new ArrayList<Object>();
		Fault fault = new Fault();
		Faultcode faultcode = new Faultcode();
		faultcode.value = new QName("faultcode");
		fault.code = faultcode;
		
		Faultreason faultreason = new Faultreason();
		faultreason.text = new ArrayList<Reasontext>();
		Reasontext reasontext = new Reasontext();
		reasontext.value = "reason1";
		reasontext.lang = "lang1";
		faultreason.text.add(reasontext);
		
		reasontext = new Reasontext();
		reasontext.value = "reason2";
		reasontext.lang = "lang2";
		faultreason.text.add(reasontext);
		
		fault.reason = faultreason;
		
		body.any.add(fault);
		
		SOAPWriter soapWriter = new SOAPWriter();
		String soap12String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap12String);
		
		SOAPReader soapReader = new SOAPReader();
		envelope = soapReader.read(Envelope.class, Bulldog.class, new ByteArrayInputStream(soap12String.getBytes()));
		
		assertNotNull(envelope.body);
		assertTrue(envelope.body.any.size() == 1);
		fault = (Fault) envelope.body.any.get(0);
		assertNotNull(fault.code);
		assertEquals(new QName("faultcode"), fault.code.value);
		
		faultreason = fault.reason;
		assertNotNull(faultreason);
		assertTrue(faultreason.text.size() == 2);
		reasontext = faultreason.text.get(0);
		assertEquals("reason1", reasontext.value);
		assertEquals("lang1", reasontext.lang);
		
		reasontext = faultreason.text.get(1);
		assertEquals("reason2", reasontext.value);
		assertEquals("lang2", reasontext.lang);
		
		soap12String = soapWriter.write(envelope);
		
		System.out.println("write result :");
		System.out.println(soap12String);
	}

}
