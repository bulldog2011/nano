package com.javacodegeeks.android.apps.moviesearchapp.services;

//import java.io.InputStream;
import java.util.List;

import android.util.Log;

import com.javacodegeeks.android.apps.moviesearchapp.model.Movie;
import com.javacodegeeks.android.apps.moviesearchapp.model.MovieSearchResult;

public class MovieSeeker extends GenericSeeker<Movie> {
		
	private static final String MOVIE_SEARCH_PATH = "Movie.search/";
	
	public List<Movie> find(String query) throws Exception {
		List<Movie> moviesList = retrieveMoviesList(query);
		return moviesList;
	}
	
	public List<Movie> find(String query, int maxResults) throws Exception {
		List<Movie> moviesList = retrieveMoviesList(query);
		return retrieveFirstResults(moviesList, maxResults);
	}
	
	private List<Movie> retrieveMoviesList(String query) throws Exception {
		String url = constructSearchUrl(query);
		String response = httpRetriever.retrieve(url);
		//InputStream is = httpRetriever.retrieveStream(url);
		Log.d(getClass().getSimpleName(), response);
		MovieSearchResult searchResult = xmlReader.read(MovieSearchResult.class, response);
		//MovieSearchResult searchResult = xmlReader.read(MovieSearchResult.class, is);
		if (searchResult.movieContainer != null) {
			return searchResult.movieContainer.movies;
		} else {
			return null;
		}
	}

	@Override
	public String retrieveSearchMethodPath() {
		return MOVIE_SEARCH_PATH;
	}

}
