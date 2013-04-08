package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.util.List;

import com.leansoft.nano.annotation.Element;

public class PeopleContainer {

	@Element(name="person")
	public List<Person> personList;
	
}
