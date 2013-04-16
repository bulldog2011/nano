package net.webservicex.service;

import net.webservicex.client.BarCodeSoap_SOAPClient;

/**
 * http://www.webservicex.net/genericbarcode.asmx
 * 
 * @author bulldog
 *
 */
public class BarCodeServiceClient {
	
	// target endpoint
	public static String barCodeServiceUrl = "http://www.webservicex.net/genericbarcode.asmx";
	
	private static volatile BarCodeSoap_SOAPClient client = null;
	
	// get a shared client
	public static BarCodeSoap_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(BarCodeServiceClient.class) {
				if (client == null) {
					client = new BarCodeSoap_SOAPClient();
					client.setEndpointUrl(barCodeServiceUrl);
				}
			}
		}
		
		return client;
	}


}
