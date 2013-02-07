package com.leansoft.nano.sax;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.leansoft.nano.person.PersonListType;
import com.leansoft.nano.person.PersonType;

public class SAXHandler extends DefaultHandler  {

	private PersonListType personList;
	
	private String tempVal;
	
	private PersonType tempPerson;
	
	public SAXHandler() {
		personList = new PersonListType();
		personList.setPerson(new ArrayList<PersonType>());
	}
	
	public PersonListType getPersonList() {
		return personList;
	}
	
	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		// reset
		tempVal = "";
		if (localName.equalsIgnoreCase("person")) {
			// create a new instance of PersonType
			tempPerson = new PersonType();
			
			String id = attrs.getValue("id");
			if (id != null) {
				tempPerson.setId(Integer.parseInt(id));
			}
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (localName.equalsIgnoreCase("person")) {
			// add it to the list
			personList.getPerson().add(tempPerson);
		} else if (localName.equalsIgnoreCase("lastName")) {
			tempPerson.setLastName(tempVal);
		} else if (localName.equalsIgnoreCase("postCode")) {
			tempPerson.setPostCode(tempVal);
		} else if (localName.equalsIgnoreCase("address1")) {
			tempPerson.setAddress1(tempVal);
		} else if (localName.equalsIgnoreCase("address2")) {
			tempPerson.setAddress2(tempVal);
		} else if (localName.equalsIgnoreCase("firstName")) {
			tempPerson.setFirstName(tempVal);
		} else if (localName.equalsIgnoreCase("country")) {
			tempPerson.setCountry(tempVal);
		} else if (localName.equals("city")) {
			tempPerson.setCity(tempVal);
		}
		
	}

}
