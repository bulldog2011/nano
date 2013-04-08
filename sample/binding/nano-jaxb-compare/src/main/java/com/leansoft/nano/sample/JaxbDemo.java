package com.leansoft.nano.sample;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.leansoft.domain.jaxb.Customer;
import com.leansoft.domain.jaxb.JaxbData;

public class JaxbDemo {

	public static void main(String[] args) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(Customer.class);
		 
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        JAXBElement<Customer> jaxbElement = new JAXBElement<Customer>(new QName("customer"), Customer.class, JaxbData.CUSTOMER);
        marshaller.marshal(jaxbElement, System.out);
	}

}
