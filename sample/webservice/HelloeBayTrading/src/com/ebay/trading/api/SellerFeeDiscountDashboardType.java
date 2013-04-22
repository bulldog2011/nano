// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.trading.api;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

/**
 * 
 * PowerSeller discount information (e.g., to show in a Seller Dashboard). As a
 * PowerSeller, you can earn discounts on your monthly invoice Final Value Fees based
 * on how well you're doing as a seller.
 * 
 */
public class SellerFeeDiscountDashboardType implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "Percent")
	@Order(value=0)
	public Float percent;	
	
	@AnyElement
	@Order(value=1)
	public List<Object> any;	
	
    
}