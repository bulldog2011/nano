package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;

import com.leansoft.nano.annotation.Default;
import com.leansoft.nano.annotation.Element;

@Default
public class Person implements Serializable {
	
	private static final long serialVersionUID = 6794898677027141412L;
	
	public String score;
	public String popularity;
	public String name;
	public String id;
	public String biography;
	public String url;
	public String version;
	public String lastModifiedAt;
	@Element(name="images")
	public ImageContainer imageContainer;

}
