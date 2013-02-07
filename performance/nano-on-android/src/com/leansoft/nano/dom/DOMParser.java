package com.leansoft.nano.dom;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.leansoft.nano.person.PersonListType;
import com.leansoft.nano.person.PersonType;

import android.util.Log;

public class DOMParser {
	
	private static DocumentBuilderFactory dbf;
	
	static {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	}
	
	public static PersonListType parse(InputStream is) {
		
		PersonListType personList = new PersonListType();
		personList.setPerson(new ArrayList<PersonType>());
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			
			Element topElement = doc.getDocumentElement();
			String namespaceURI = topElement.getNamespaceURI();
			
			NodeList persons = topElement.getElementsByTagNameNS(namespaceURI, "person");
			
			for(int elementIndex = 0; elementIndex < persons.getLength(); elementIndex++) {
				Element personElement = (Element)persons.item(elementIndex);
				PersonType currentPerson = new PersonType();
				if (personElement.hasAttribute("id")) {
					String id = personElement.getAttribute("id");
					currentPerson.setId(Integer.parseInt(id));
				}
				
				String lastName = personElement.getElementsByTagNameNS(namespaceURI, "lastName").item(0).getFirstChild().getNodeValue();
				currentPerson.setLastName(lastName);
				String postCode = personElement.getElementsByTagNameNS(namespaceURI, "postCode").item(0).getFirstChild().getNodeValue();
				currentPerson.setPostCode(postCode);
				String address1 = personElement.getElementsByTagNameNS(namespaceURI, "address1").item(0).getFirstChild().getNodeValue();
				currentPerson.setAddress1(address1);
				String address2 = personElement.getElementsByTagNameNS(namespaceURI, "address2").item(0).getFirstChild().getNodeValue();
				currentPerson.setAddress2(address2);
				String firstName = personElement.getElementsByTagNameNS(namespaceURI, "firstName").item(0).getFirstChild().getNodeValue();
				currentPerson.setFirstName(firstName);
				String country = personElement.getElementsByTagNameNS(namespaceURI, "country").item(0).getFirstChild().getNodeValue();
				currentPerson.setCountry(country);
				String city = personElement.getElementsByTagNameNS(namespaceURI, "city").item(0).getFirstChild().getNodeValue();
				currentPerson.setCity(city);
				
				personList.getPerson().add(currentPerson);
				
			}
			
		} catch (Exception ex) {
			Log.d("DOM Parser", "parse failed");
		}
		
		return personList;
	}

}
