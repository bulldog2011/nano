package com.leansoft.nano.sample.domain;

import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Default;

@Default
public class Organization {
	
	private String name;
	
	private Address address = new Address();
	
	private List<Person> staff = new ArrayList<Person>();
	
	@Attribute
	private int count;
	
	public Organization() {
		
	}

	public Organization(String name, Address address) {
		this.name = name;
		this.address = address;
	}
	
	public void add(Person person) {
		staff.add(person);
	}
	
	public String getName() {
		return name;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public List<Person> getStaff() {
		return staff;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
}
