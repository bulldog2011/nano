package com.leansoft.nano.sample;

import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

@RootElement
public class Customer {
	
	@Element
	private String name;
	
	@Element
	private int age;
	
	@Attribute
	private int id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return "[name=" + name +", age=" + age + ", id=" + id + "]";
	}
}
