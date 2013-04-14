package com.javacodegeeks.android.apps.moviesearchapp.services;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.NanoFactory;

public abstract class GenericSeeker<E> {
	
	protected static final String BASE_URL = "http://api.themoviedb.org/2.1/";	
	protected static final String LANGUAGE_PATH = "en/";
	protected static final String XML_FORMAT = "xml/";
	protected static final String API_KEY = "<YOUR-API-KEY-HERE>";
	protected static final String SLASH = "/";
	
	protected HttpRetriever httpRetriever = new HttpRetriever();
	
	protected IReader xmlReader = NanoFactory.getXMLReader();
	
	
	public abstract List<E> find(String query) throws Exception;
	public abstract List<E> find(String query, int maxResults) throws Exception;

	public abstract String retrieveSearchMethodPath();
	
	protected String constructSearchUrl(String query) {
		StringBuffer sb = new StringBuffer();
		sb.append(BASE_URL);
		sb.append(retrieveSearchMethodPath());
		sb.append(LANGUAGE_PATH);
		sb.append(XML_FORMAT);
		sb.append(API_KEY);
		sb.append(SLASH);
		sb.append(URLEncoder.encode(query));
		return sb.toString();
	}
	
	public List<E> retrieveFirstResults(List<E> list, int maxResults) {
		ArrayList<E> newList = new ArrayList<E>();
		if (list == null) {
			return newList;
		}
		int count = Math.min(list.size(), maxResults);
		for (int i=0; i<count; i++) {
			newList.add(list.get(i));
		}
		return newList;
	}

}
