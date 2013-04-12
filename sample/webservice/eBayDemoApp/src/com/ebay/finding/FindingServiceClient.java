package com.ebay.finding;

import com.ebay.finding.api.client.FindingServicePortType_SOAPClient;
import com.leansoft.nano.ws.SOAPVersion;

public class FindingServiceClient {
	
	public static String eBayFindingServiceURLString = "http://svcs.ebay.com/services/search/FindingService/v1";
	public static String eBayAppId = "YOUR APPID HERE";
	
	private static volatile FindingServicePortType_SOAPClient client = null;
	
	public static FindingServicePortType_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(FindingServiceClient.class) {
				if (client == null) {
					client = new FindingServicePortType_SOAPClient();
					client.setEndpointUrl(eBayFindingServiceURLString);
					client.setSoapVersion(SOAPVersion.SOAP12); // ebay finding service supports SOAP 1.2
					client.setContentType("application/soap+xml");
					client.getAsyncHttpClient().addHeader("Accept", "application/soap+xml");
					client.getAsyncHttpClient().addHeader("X-EBAY-SOA-SECURITY-APPNAME", eBayAppId);
					client.getAsyncHttpClient().addHeader("X-EBAY-SOA-MESSAGE-PROTOCOL", "SOAP12");
					client.getAsyncHttpClient().addHeader("X-EBAY-SOA-REQUEST-DATA-FORMAT", "SOAP");
				}
			}
		}
		
		return client;
	}
	
}
