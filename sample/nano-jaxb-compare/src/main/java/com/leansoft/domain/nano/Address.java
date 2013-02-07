package com.leansoft.domain.nano;

import com.leansoft.nano.annotation.Element;

public class Address {
	
	@Element
	private String city;

	@Element
	private String street;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	
}
