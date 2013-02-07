package com.leansoft.domain.jaxb;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"street", "city"})
public class Address {
	
	private String city;
	
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
