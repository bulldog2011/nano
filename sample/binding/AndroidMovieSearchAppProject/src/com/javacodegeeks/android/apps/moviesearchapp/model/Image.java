package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;

import com.leansoft.nano.annotation.Attribute;

public class Image implements Serializable {
	
	private static final long serialVersionUID = -5274909668022888191L;
	
	public static final String SIZE_ORIGINAL = "original";
	public static final String SIZE_MID = "mid";
	public static final String SIZE_COVER = "cover";
	public static final String SIZE_THUMB = "thumb";

	public static final String TYPE_PROFILE = "profile";
	public static final String TYPE_POSTER = "poster";

	@Attribute
	public String type;
	
	@Attribute
	public String url;
	
	@Attribute
	public String size;
	
	@Attribute
	public int width;
	
	@Attribute
	public int height;
	
	@Attribute
	public String id;

}
