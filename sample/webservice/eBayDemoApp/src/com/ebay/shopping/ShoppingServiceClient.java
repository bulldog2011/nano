package com.ebay.shopping;

import com.ebay.shopping.api.client.ShoppingInterface_XMLClient;

public class ShoppingServiceClient {
	
	// production
	public static String eBayShoppingServiceURLString = "http://open.api.ebay.com/shopping?";
	// sandbox
	//public static final String eBayShoppingServiceURLString = "http://open.api.sandbox.ebay.com/shopping";
	public static String eBayAppId = "YOUR APPID HERE";
	public static String targetAPIVersion = "809";
	/**
	for site id list, see http://developer.ebay.com/DevZone/shopping/docs/CallRef/types/SiteCodeType.html
	*/
	public static String targetSiteid = "0";
	
	private static volatile ShoppingInterface_XMLClient client = null;
	
	public static ShoppingInterface_XMLClient getSharedClient() {
		if (client == null) {
			synchronized(ShoppingServiceClient.class) {
				if (client == null) {
					client = new ShoppingInterface_XMLClient();
					client.setEndpointUrl(eBayShoppingServiceURLString);
					client.getAsyncHttpClient().addHeader("X-EBAY-API-APP-ID", eBayAppId);
					client.getAsyncHttpClient().addHeader("X-EBAY-API-REQUEST-ENCODING", "XML");
					client.getAsyncHttpClient().addHeader("X-EBAY-API-VERSION", targetAPIVersion);
					client.getAsyncHttpClient().addHeader("X-EBAY-API-SITE-ID", targetSiteid);
				}
			}
		}
		
		return client;
	}

}
