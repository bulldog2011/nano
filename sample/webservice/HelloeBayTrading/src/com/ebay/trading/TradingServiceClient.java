package com.ebay.trading;

import java.util.ArrayList;
import java.util.List;

import com.ebay.trading.api.CustomSecurityHeaderType;
import com.ebay.trading.api.client.EBayAPIInterface_SOAPClient;

public class TradingServiceClient {
	
	// production
	//public static String eBayTradingServiceURLString  = "https://api.ebay.com/wsapi";
	// sandbox
	public static final String eBayTradingServiceURLString  = "https://api.sandbox.ebay.com/wsapi";
	public static String eBayAppId = "YOUR APPID HERE";
	public static String eBayAuthToken = "YOUR AUTH TOKEN HERE";
	public static String targetAPIVersion = "809";
	/**
	for site id list, see http://developer.ebay.com/DevZone/shopping/docs/CallRef/types/SiteCodeType.html
	*/
	public static String targetSiteid = "0";
	
	private static volatile EBayAPIInterface_SOAPClient client = null;
	
	public static EBayAPIInterface_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(TradingServiceClient.class) {
				if (client == null) {
					client = new EBayAPIInterface_SOAPClient();
					client.setEndpointUrl(eBayTradingServiceURLString);
					client.getAsyncHttpClient().addHeader("Accept", "application/soap+xml");
					client.getAsyncHttpClient().addHeader("Content-Type", "application/soap+xml");
					client.getAsyncHttpClient().addHeader("SOAPAction", "");


					client.addUrlParam("siteid", targetSiteid);
					client.addUrlParam("version", targetAPIVersion);
					client.addUrlParam("appid", eBayAppId);
					client.addUrlParam("Routing", "new");
					
					CustomSecurityHeaderType customHeader = new CustomSecurityHeaderType();
					customHeader.eBayAuthToken = eBayAuthToken;
					List<Object> customSOAPHeaders = new ArrayList<Object>();
					customSOAPHeaders.add(customHeader);
					client.setCustomSOAPHeaders(customSOAPHeaders);
					
					// for some calls like FetchToken, RevokeToken, GetTokenStatus, and GetSessionID,
				    // you also need to add DevId, AppId and AuthCert in the custom header.
				}
			}
		}
		
		return client;
	}

}
