package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;
import java.util.List;

import com.leansoft.nano.annotation.Element;

public class ImageContainer implements Serializable {
	
	private static final long serialVersionUID = 4785761630642157908L;
	
	@Element(name="image")
	public List<Image> imageList;

}
