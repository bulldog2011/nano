// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.trading.api;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

/**
 * 
 * This type is deprecated as <b>GetProduct*</b> calls were deprecated.
 * 
 * 
 */
public class SearchAttributesType implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "AttributeID")
	@Order(value=0)
	public int attributeID;	
	
	@Element(name = "DateSpecifier")
	@Order(value=1)
	public DateSpecifierCodeType dateSpecifier;	
	
	@Element(name = "RangeSpecifier")
	@Order(value=2)
	public RangeCodeType rangeSpecifier;	
	
	@Element(name = "ValueList")
	@Order(value=3)
	public List<ValType> valueList;	
	
	@AnyElement
	@Order(value=4)
	public List<Object> any;	
	
    
}