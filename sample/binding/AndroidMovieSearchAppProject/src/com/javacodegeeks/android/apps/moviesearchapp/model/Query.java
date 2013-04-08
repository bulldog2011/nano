package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;

import com.leansoft.nano.annotation.Attribute;

public class Query implements Serializable {

	private static final long serialVersionUID = 7875169023013805580L;
	
	@Attribute
	public String searchTerms;
	
}
