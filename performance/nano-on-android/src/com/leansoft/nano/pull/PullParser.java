package com.leansoft.nano.pull;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.leansoft.nano.person.PersonListType;
import com.leansoft.nano.person.PersonType;

public class PullParser {
	
    private static XmlPullParserFactory pullMaker;
    
    static {
    	try {
			pullMaker = XmlPullParserFactory.newInstance();
			pullMaker.setNamespaceAware(true);
		} catch (XmlPullParserException e) {
			Log.d("Pull Parser", "parse failed");
		}
    }
	
	public static PersonListType parse(InputStream is) {
		
		PersonListType personList = new PersonListType();
		personList.setPerson(new ArrayList<PersonType>());
		try {
			
			XmlPullParser parser = pullMaker.newPullParser();
			
			parser.setInput(is, null);
			
			String tempVal = null;
			PersonType tempPerson = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
            		// reset
            		tempVal = "";
            		if (parser.getName().equalsIgnoreCase("person")) {
            			// create a new instance of PersonType
            			tempPerson = new PersonType();
            			
            			String id = parser.getAttributeValue(null, "id");
            			if (id != null) {
            				tempPerson.setId(Integer.parseInt(id));
            			}
            		}
                	
                	break;
                case XmlPullParser.END_TAG:
            		if (parser.getName().equalsIgnoreCase("person")) {
            			// add it to the list
            			personList.getPerson().add(tempPerson);
            		} else if (parser.getName().equalsIgnoreCase("lastName")) {
            			tempPerson.setLastName(tempVal);
            		} else if (parser.getName().equalsIgnoreCase("postCode")) {
            			tempPerson.setPostCode(tempVal);
            		} else if (parser.getName().equalsIgnoreCase("address1")) {
            			tempPerson.setAddress1(tempVal);
            		} else if (parser.getName().equalsIgnoreCase("address2")) {
            			tempPerson.setAddress2(tempVal);
            		} else if (parser.getName().equalsIgnoreCase("firstName")) {
            			tempPerson.setFirstName(tempVal);
            		} else if (parser.getName().equalsIgnoreCase("country")) {
            			tempPerson.setCountry(tempVal);
            		} else if (parser.getName().equals("city")) {
            			tempPerson.setCity(tempVal);
            		}
                	
                	break;
                	
                case XmlPullParser.TEXT:
                	tempVal = parser.getText();
                	break;
                    
				}
                eventType = parser.next();
			}
			
			
		} catch (Exception ex) {
			Log.d("Pull Parser", "parse failed");
		}
		
		return personList;
		
	}

}
