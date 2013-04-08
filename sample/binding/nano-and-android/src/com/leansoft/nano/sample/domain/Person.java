package com.leansoft.nano.sample.domain;

import com.leansoft.nano.annotation.Default;

@Default
public class Person {
	
	private String name;
	private Address address = new Address();
	
	public Person() {
		
	}
	
	public Person(String name, Address address) {
		this.name = name;
		this.address = address;
	}

}
