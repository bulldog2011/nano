// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01.cartaddrequest;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import com.amazon.webservices.awsecommerceservice._2011_08_01.cartaddrequest.items.Item;
import java.util.List;

public class Items implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "Item")
	public List<Item> item;	
	
    
}