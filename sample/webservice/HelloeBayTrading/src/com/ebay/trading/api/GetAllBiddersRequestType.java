// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.trading.api;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

/**
 * 
 * Provides three modes for retrieving a list of the users that bid on
 * a listing.
 * 
 */
@RootElement(name = "GetAllBiddersRequest", namespace = "urn:ebay:apis:eBLBaseComponents")
public class GetAllBiddersRequestType extends AbstractRequestType implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "ItemID")
	@Order(value=0)
	public String itemID;	
	
	@Element(name = "CallMode")
	@Order(value=1)
	public GetAllBiddersModeCodeType callMode;	
	
	@Element(name = "IncludeBiddingSummary")
	@Order(value=2)
	public Boolean includeBiddingSummary;	
	
    
}