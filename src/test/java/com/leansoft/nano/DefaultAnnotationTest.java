package com.leansoft.nano;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Default;

public class DefaultAnnotationTest extends TestCase {
	
	@Default
	private static class Employee {
		@Attribute
		public int id;
		
		public String firstName;
		
		public String lastName;
		
		public Calendar birthday;
		
		public int age;
		
		public Responsibility responsibilities;
		
	}
	
	@Default
	private static class Responsibility {
		public List<String> responsibilityList;
	}
	
	public void testDefaultAnnotation() throws Exception {
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		IReader xmlReader = NanoFactory.getXMLReader();
		verifyDefaultAnnotation(xmlWriter, xmlReader);
		IWriter jsonWriter = NanoFactory.getJSONWriter();
		IReader jsonReader = NanoFactory.getJSONReader();
		verifyDefaultAnnotation(jsonWriter, jsonReader);
	}
		
	
	public void verifyDefaultAnnotation(IWriter writer, IReader reader) throws Exception {
		
		Employee employee = new Employee();
		employee.id = 120;
		employee.firstName = "Jack";
		employee.lastName = "zhang";
		employee.birthday = Calendar.getInstance();
		employee.age = 30;
		employee.responsibilities = new Responsibility();
		employee.responsibilities.responsibilityList = new ArrayList<String>();
		employee.responsibilities.responsibilityList.add("team leader");
		employee.responsibilities.responsibilityList.add("software development");
		employee.responsibilities.responsibilityList.add("architect");
		
    	StringWriter stringWriter = new StringWriter();
		writer.write(employee, stringWriter);
		
		Employee employeeCopy = reader.read(Employee.class, stringWriter.toString());
		
		assertTrue(employeeCopy.id == 120);
		assertEquals("Jack", employeeCopy.firstName);
		assertEquals("zhang", employeeCopy.lastName);
		assertEquals(employee.birthday, employeeCopy.birthday);
		assertTrue(employeeCopy.age == 30);
		assertTrue(employeeCopy.responsibilities.responsibilityList.size() == 3);
	}

}
