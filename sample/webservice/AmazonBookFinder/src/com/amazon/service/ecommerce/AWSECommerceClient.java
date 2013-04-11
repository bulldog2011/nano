package com.amazon.service.ecommerce;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.leansoft.nano.util.Base64;

public class AWSECommerceClient {
	
	/**
	    Update url according to your local location, see a list of supported location at the end of the wsdl:
	    http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl
	*/
	public static String AWSCEServiceURLString = "https://webservices.amazon.com/onca/soap?Service=AWSECommerceService";
	
    /** Use this to specify the AWS Access Key ID */
	public static String AWSAccessKeyId = "YOUR ACCESS KEY HERE";
    /** Use this to specify the AWS Secret Key */
	public static String AWSSecureKeyId = "YOUR SECURE KEY HERE";
	
    /** Namespace for all AWS Security elements */
	public static final String AuthHeaderNS = "http://security.amazonaws.com/doc/2007-01-01/";
	
	private static AWSECommerceServicePortType_SOAPClient client = null;
	
    /** Algorithm used to calculate string hashes */
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    
    private static Mac mac = null;
	
	public static AWSECommerceServicePortType_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(AWSECommerceClient.class) {
				if (client == null) {
					client = new AWSECommerceServicePortType_SOAPClient();
					client.setEndpointUrl(AWSCEServiceURLString);
					
					// init security
					try {
						byte[] bytes = AWSSecureKeyId.getBytes("UTF-8");
						SecretKeySpec keySpec = new SecretKeySpec(bytes, SIGNATURE_ALGORITHM);
						mac = Mac.getInstance(SIGNATURE_ALGORITHM);
						mac.init(keySpec);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					} catch (NoSuchAlgorithmException e) {
						throw new RuntimeException(e);
					} catch (InvalidKeyException e) {
						throw new RuntimeException(e);
					}
					
				}
			}
		}
		return client;
	}
	

	/**
	Authentication of SOAP request
	see details here: http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
	*/
	public static void authenticateRequest(String action) {
		String timestamp = getTimestamp();
		String signature = calculateSignature(action, timestamp);
		
		List<Object> securityHeaders = new ArrayList<Object>();
		Document document = getDocument();
		Element accessKeyElement = document.createElementNS(AuthHeaderNS, "AWSAccessKeyId");
		accessKeyElement.appendChild(document.createTextNode(AWSAccessKeyId));
		securityHeaders.add(accessKeyElement);
		
		Element timestampElement = document.createElementNS(AuthHeaderNS, "Timestamp");
		timestampElement.appendChild(document.createTextNode(timestamp));
		securityHeaders.add(timestampElement);
		
		Element signatureElement = document.createElementNS(AuthHeaderNS, "Signature");
		signatureElement.appendChild(document.createTextNode(signature));
		securityHeaders.add(signatureElement);
		
		client.setCustomSOAPHeaders(securityHeaders);
	}
	
    /**
     * Calculates a time stamp from "now" in UTC and returns it in ISO8601 string
     * format. The soap message expires 15 minutes after this time stamp.
     * AWS only supports UTC and it's canonical representation as 'Z' in an
     * ISO8601 time string. E.g.  2008-02-10T23:59:59Z
     * 
     * See http://www.w3.org/TR/xmlschema-2/#dateTime for more details.
     * 
     * @return ISO8601 time stamp string for "now" in UTC.
     */
    private static String getTimestamp() {
        Calendar         calendar  = Calendar.getInstance();
        SimpleDateFormat is08601   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        is08601.setTimeZone(TimeZone.getTimeZone("UTC"));
        return is08601.format(calendar.getTime());
    }
	
    /**
     * Method borrowed from AWS example code at http://developer.amazonwebservices.com/
     * @param  action The single SOAP body element that is the action the
     *                      request is taking.
     * @param  timestamp  The time stamp string as provided in the &lt;aws:Timestamp&gt;
     *                      header element.
     * @return A hash calculated according to AWS security rules to be provided in the
     *         &lt;aws:signature&gt; header element.
     * @throws Exception If there were errors or missing, required classes when
     *                   trying to calculate the hash.
     */
    private static String calculateSignature(String action, String timestamp) {
        String toSign = (action + timestamp);

        byte[] sigBytes = mac.doFinal(toSign.getBytes());
        return new String(Base64.encode(sigBytes));
    }
    
    
    /**
     * Get a W3C XML Document
     * 
     * @return a document
     */
    private static Document getDocument() {
        try {
        	
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	dbf.setNamespaceAware(true);
        	
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	return db.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(
					"Failed to create DocumentBuilder!", e);
		}
    }
}
