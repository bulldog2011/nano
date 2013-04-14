package com.leansoft.nano.sample.domain;

import com.leansoft.nano.annotation.Default;

@Default
public class Address {

	private String street;
	private String code;
	private String city;

	public Address() {
		
	}
	
	public Address(String street, String code, String city) {
		this.street = street;
		this.code = code;
		this.city = city;
	}
	
}
