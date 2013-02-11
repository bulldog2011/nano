package com.javacodegeeks.android.apps.moviesearchapp.services;

//import java.io.InputStream;
import java.util.List;

import android.util.Log;

import com.javacodegeeks.android.apps.moviesearchapp.model.PeopleSearchResult;
import com.javacodegeeks.android.apps.moviesearchapp.model.Person;

public class PersonSeeker extends GenericSeeker<Person> {
	
	private static final String PERSON_SEARCH_PATH = "Person.search/";
	
	public List<Person> find(String query) throws Exception {
		List<Person> personList = retrievePersonList(query);
		return personList;
	}
	
	public List<Person> find(String query, int maxResults) throws Exception {
		List<Person> personList = retrievePersonList(query);
		return retrieveFirstResults(personList, maxResults);
	}
	
	private List<Person> retrievePersonList(String query) throws Exception {
		String url = constructSearchUrl(query);
		String response = httpRetriever.retrieve(url);
		//InputStream is = httpRetriever.retrieveStream(url);
		Log.d(getClass().getSimpleName(), response);
		PeopleSearchResult searchResult = xmlReader.read(PeopleSearchResult.class, response);
		//PeopleSearchResult searchResult = xmlReader.read(PeopleSearchResult.class, is);
		if (searchResult.peopleContainer != null) {
			return searchResult.peopleContainer.personList;
		} else {
			return null;
		}
	}

	@Override
	public String retrieveSearchMethodPath() {
		return PERSON_SEARCH_PATH;
	}

}
