package com.webservicemart.service;

import com.webservicemart.ws.client.USZipSoap_SOAPClient;

/**
 * http://www.webservicemart.com/uszip.asmx
 * 
 * @author bulldog
 *
 */
public class USZipValidatorServiceClient {
	
	// target endpoint
	public static String usZipValidatorServiceUrl = "http://www.webservicemart.com/uszip.asmx";
	
	private static volatile USZipSoap_SOAPClient client = null;
	
	// get a shared client
	public static USZipSoap_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(USZipValidatorServiceClient.class) {
				if (client == null) {
					client = new USZipSoap_SOAPClient();
					client.setEndpointUrl(usZipValidatorServiceUrl);
				}
			}
		}
		
		return client;
	}

}
