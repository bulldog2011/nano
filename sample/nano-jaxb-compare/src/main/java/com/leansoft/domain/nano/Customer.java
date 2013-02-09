package com.leansoft.domain.nano;

import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Default;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

@Default
@RootElement(namespace="http://www.example.com")
public class Customer {

	@Attribute
	private long id;
	
	private String name;
	
	private Address address;
	
	@Element(name="phone-number")
	private List<PhoneNumber> phoneNumbers;
	
	public Customer() {
		phoneNumbers = new ArrayList<PhoneNumber>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
}
