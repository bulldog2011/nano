package com.leansoft.nano;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.leansoft.nano.exception.ReaderException;
import com.leansoft.nano.impl.SOAPReader;

import junit.framework.TestCase;

public class SOAPReaderTest extends TestCase {
	
	public String SOAP_11_MESSAGE = "<?xml version='1.0' encoding='UTF-8'?>" + 
									"<SOAP-ENV:Envelope " + 
									  "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " + 
									  "xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" " + 
									  "xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">" +
									  "<SOAP-ENV:Body>" + 
									     "<SOAP-ENV:Fault>" + 
									     "<faultcode xsi:type=\"xsd:string\">SOAP-ENV:Client</faultcode>" +
									     "<faultstring xsi:type=\"xsd:string\">" + 
									     "     Failed to locate method (ValidateCreditCard) in class" +
									     "     (examplesCreditCard) at /usr/local/ActivePerl-5.6/lib/" +
									     "       site_perl/5.6.0/SOAP/Lite.pm line 1555." +
									     "   </faultstring>" +				
									     " </SOAP-ENV:Fault>" + 
									  "</SOAP-ENV:Body>" + 
									"</SOAP-ENV:Envelope>";
	
	public String SOAP_12_MESSAGE = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" + 
									"<env:Header/><env:Body>" + 
									"<env:Fault>" + 
									"<env:Code><env:Value>env:Sender</env:Value></env:Code>" +
									"<env:Reason><env:Text xml:lang=\"en-US\">" +
									"Message does not have necessary info" +
									"</env:Text></env:Reason>" +
									"<env:Role>http://gizmos.com/order</env:Role>" +
									"<env:Detail>" +
									"<PO:order xmlns:PO=\"http://gizmos.com/orders/\">" +
									"Quantity element does not have a value</PO:order>" +
									"<PO:confirmation xmlns:PO=\"http://gizmos.com/confirm\">" +
									"Incomplete address: no zip code</PO:confirmation>" +
									"</env:Detail></env:Fault>" +
									"</env:Body></env:Envelope>";
	
	
	public void testReadSOAP11FaultMessage() throws Exception {
		SOAPReader soapReader = new SOAPReader();
		InputStream is = new ByteArrayInputStream(SOAP_11_MESSAGE.getBytes("UTF-8"));
		com.leansoft.nano.soap11.Envelope envelope = soapReader.read(com.leansoft.nano.soap11.Envelope.class, Bulldog.class, is);
		assertTrue(envelope.body.any.size() == 1);
		Object object = envelope.body.any.get(0);
		assertTrue(object instanceof com.leansoft.nano.soap11.Fault);
		com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) object;
		assertEquals(fault.faultcode.getLocalPart(), "Client");
		assertEquals(fault.faultcode.getPrefix(), "SOAP-ENV");
		
		is = new ByteArrayInputStream(SOAP_11_MESSAGE.getBytes("UTF-8"));
		try {
			com.leansoft.nano.soap12.Envelope envelope12 = soapReader.read(com.leansoft.nano.soap12.Envelope.class, Bulldog.class, is);
			fail("should throw ReaderException");
		} catch (ReaderException re) {
			// expected
		}

	}
	
	public void testReadeSOAP12FaultMessage() throws Exception {
		SOAPReader soapReader = new SOAPReader();
		InputStream is = new ByteArrayInputStream(SOAP_12_MESSAGE.getBytes("UTF-8"));
		com.leansoft.nano.soap12.Envelope envelope = soapReader.read(com.leansoft.nano.soap12.Envelope.class, Bulldog.class, is);
		assertTrue(envelope.body.any.size() == 1);
		Object object = envelope.body.any.get(0);
		assertTrue(object instanceof com.leansoft.nano.soap12.Fault);
		com.leansoft.nano.soap12.Fault fault = (com.leansoft.nano.soap12.Fault) object;
		assertEquals(fault.role, "http://gizmos.com/order");
		assertTrue(fault.reason.text.size() == 1);
		assertEquals(fault.reason.text.get(0).value, "Message does not have necessary info");
		
		is = new ByteArrayInputStream(SOAP_12_MESSAGE.getBytes("UTF-8"));
		try {
			com.leansoft.nano.soap11.Envelope envelope11 = soapReader.read(com.leansoft.nano.soap11.Envelope.class, Bulldog.class, is);
			fail("should throw ReaderException");
		} catch (ReaderException re) {
			// expected
		}
	}

}
