package com.leansoft.nano.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.leansoft.nano.Format;
import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;

public class NanoExample {

	public static void main(String[] args) {
		
		Customer customer = new Customer();
		customer.setId(100);
		customer.setName("bulldog");
		customer.setAge(30);
		
		// Marshalling
		try {
			
			File xmlFile = new File("D:\\custom_file.xml");
			
			// for pretty output
			Format format = new Format(true);
			IWriter xmlWriter = NanoFactory.getXMLWriter(format);
			
			xmlWriter.write(customer, new FileOutputStream(xmlFile));
			System.out.println("xml output : ");
			xmlWriter.write(customer, System.out);
			System.out.println();
			
			File jsonFile = new File("D:\\custom_file.json");
			
			IWriter jsonWriter = NanoFactory.getJSONWriter();
			
			jsonWriter.write(customer, new FileOutputStream(jsonFile));
			System.out.println("json output : ");
			jsonWriter.write(customer, System.out);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.println();
		
		// Unmarshalling
		try {
			
			File xmlFile = new File("D:\\custom_file.xml");
			
			IReader xmlReader = NanoFactory.getXMLReader();
			
			customer = xmlReader.read(Customer.class, new FileInputStream(xmlFile));
			System.out.println("customer read from xml : ");
			System.out.println(customer);
			
			File jsonFile = new File("D:\\custom_file.json");
			
			IReader jsonReader = NanoFactory.getJSONReader();
			
			customer = jsonReader.read(Customer.class, new FileInputStream(jsonFile));
			System.out.println("customer read from json : ");
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
