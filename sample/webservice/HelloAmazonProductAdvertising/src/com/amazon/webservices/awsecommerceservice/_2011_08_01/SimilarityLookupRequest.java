// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

public class SimilarityLookupRequest implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "Condition")
	public String condition;	
	
	@Element(name = "ItemId")
	public List<String> itemId;	
	
	@Element(name = "MerchantId")
	public String merchantId;	
	
	@Element(name = "ResponseGroup")
	public List<String> responseGroup;	
	
	@Element(name = "SimilarityType")
	public String similarityType;	
	
    
}