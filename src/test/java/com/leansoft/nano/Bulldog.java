package com.leansoft.nano;

import java.util.List;

import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;

@RootElement(name="MyPet", namespace="a.b.c")
public class Bulldog {

	@Element(name="Name")
	public String name;
	
	@Attribute(name="Age")
	public Integer age;
	
	@Attribute(name="Color")
	public String color;
	
	@Element(name="Desc")
	public String desc;
	
	@Element(name="Children")
	public List<Bulldog> children;
	
}
