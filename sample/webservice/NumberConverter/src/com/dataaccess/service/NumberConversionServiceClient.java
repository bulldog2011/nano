package com.dataaccess.service;

import com.dataaccess.webservicesserver.client.NumberConversionSoapType_SOAPClient;

/**
 * The Number Conversion Web Service, implemented with Visual DataFlex, 
 * provides functions that convert numbers into words or dollar amounts.
 * 
 * http://www.dataaccess.com/webservicesserver/numberconversion.wso
 * 
 * @author bulldog
 *
 */
public class NumberConversionServiceClient {
	
	// target endpoint
	public static String numberConversionServiceUrl = "http://www.dataaccess.com/webservicesserver/numberconversion.wso";
	
	private static volatile NumberConversionSoapType_SOAPClient client = null;
	
	// get a shared client
	public static NumberConversionSoapType_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(NumberConversionServiceClient.class) {
				if (client == null) {
					client = new NumberConversionSoapType_SOAPClient();
					client.setEndpointUrl(numberConversionServiceUrl);
				}
			}
		}
		
		return client;
	}

}
