package com.leansoft.nano.sample;

import com.leansoft.domain.nano.NanoData;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;

public class NanoDemo {

	public static void main(String[] args) throws Exception {
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		xmlWriter.write(NanoData.CUSTOMER, System.out);
		
		IWriter jsonWriter = NanoFactory.getJSONWriter();
		jsonWriter.write(NanoData.CUSTOMER, System.out);
	}

}
