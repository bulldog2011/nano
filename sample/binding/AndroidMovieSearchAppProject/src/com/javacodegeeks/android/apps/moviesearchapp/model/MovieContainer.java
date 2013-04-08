package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;
import java.util.List;

import com.leansoft.nano.annotation.Element;

public class MovieContainer implements Serializable {
	
	private static final long serialVersionUID = -957664961371798735L;
	
	@Element(name="movie")
	public List<Movie> movies;

}
