package com.leansoft.nano.perftest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.leansoft.jaxb.person.ObjectFactory;
import com.leansoft.jaxb.person.PersonType;
import com.leansoft.jaxb.person.PersonsType;
import com.leansoft.nano.Format;
import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Performance benchmark between Nano and JAXB
 *
 */
public class PerfRunner 
{
	
	private static final String OUTPUT_FOLDER = System.getProperty("user.home") + File.separator + "nano-vs-jaxb";
	
	private static final Logger LOG = Logger.getLogger(PerfRunner.class);
	
	private static JAXBContext jaxbContext = null;
	private static Marshaller jaxbMarshaller = null;
	private static Unmarshaller jaxbUnmarshaller = null;
	
	private static IWriter nanoXmlWriter = null;
	private static IWriter nanoJsonWriter = null;
	
	private static IReader nanoXmlReader = null;
	private static IReader nanoJsonReader = null;
	
	private void init() throws JAXBException {
		jaxbContext = JAXBContext.newInstance("com.leansoft.jaxb.person");
		jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		
		Format format = new Format(true);
		nanoXmlWriter = NanoFactory.getXMLWriter(format);
		nanoJsonWriter = NanoFactory.getJSONWriter(format);
		
		nanoXmlReader = NanoFactory.getXMLReader();
		nanoJsonReader = NanoFactory.getJSONReader();
	}
	
    public static void main( String[] args )
    {
		try {
    	
			File outputDir = new File(OUTPUT_FOLDER);
			if (!outputDir.exists()) {
				LOG.info("Creating output folder: "
						+ outputDir.getAbsolutePath());
				boolean created = outputDir.mkdirs();
				if (!created) {
					throw new IllegalStateException("Could not create "
							+ outputDir.getAbsolutePath() + ". Aborting...");
				}
			}
			
	    	PerfRunner runner = new PerfRunner();
	    	runner.init();
		
	    	runner.marshallTest();
	    	
			runner.unmarshallTest();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
    private void marshallTest() throws Exception {
    	this.nanoXmlMarshall(1000, "nano-xml-1000.xml");
    	this.nanoXmlMarshall(10000, "nano-xml-10000.xml");
    	this.nanoXmlMarshall(100000, "nano-xml-100000.xml");
    	
    	this.nanoJsonMarshall(1000, "nano-json-1000.xml");
    	this.nanoJsonMarshall(10000, "nano-json-10000.xml");
    	this.nanoJsonMarshall(100000, "nano-json-100000.xml");
    	
    	this.jaxbMarshall(1000, "jaxb-1000.xml");
    	this.jaxbMarshall(10000, "jaxb-10000.xml");
    	this.jaxbMarshall(100000, "jaxb-100000.xml");
    }
    
    private void unmarshallTest() throws Exception {
    	
    	this.jaxbUnmarshall("jaxb-1000.xml");
    	this.jaxbUnmarshall("jaxb-10000.xml");
    	this.jaxbUnmarshall("jaxb-100000.xml");
    	
    	this.nanoXmlUnmarshall("nano-xml-1000.xml");
    	this.nanoXmlUnmarshall("nano-xml-10000.xml");
    	this.nanoXmlUnmarshall("nano-xml-100000.xml");
    	
    	this.nanoJsonUnmarshall("nano-json-1000.xml");
    	this.nanoJsonUnmarshall("nano-json-10000.xml");
    	this.nanoJsonUnmarshall("nano-json-100000.xml");
    }
    
    
    private PersonsType getJaxbPersons(int numberOfRecords) {
		PersonsType persons = new ObjectFactory().createPersonsType();
		List<PersonType> personList = persons.getPerson();
		PodamFactory factory = new PodamFactoryImpl();
		for (int i = 0; i < numberOfRecords; i++) {
			personList.add(factory.manufacturePojo(PersonType.class));
		}
		return persons;
    }
    
    private void jaxbMarshall(int numberOfRecords, String fileName) throws Exception {
    	PersonsType persons = this.getJaxbPersons(numberOfRecords);
		JAXBElement<PersonsType> personsElement = new ObjectFactory()
		.createPersons(persons);
    	
    	
		File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file), 4096);

		long start = System.currentTimeMillis();
		
		try {
			jaxbMarshaller.marshal(personsElement, bos);
			bos.flush();

			long end = System.currentTimeMillis();

			LOG.info("JAXB Marshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));
		} finally {
			IOUtils.closeQuietly(bos);
		}
    }
    
    private void jaxbUnmarshall(String fileName) throws Exception {
    	File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
    	
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		
		long start = System.currentTimeMillis();
		
		try {
			JAXBElement<PersonsType> root = (JAXBElement<PersonsType>) jaxbUnmarshaller
					.unmarshal(bis);

			int numberOfRecords = root.getValue().getPerson().size();

			long end = System.currentTimeMillis();

			LOG.info("JAXB Unmarshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));

		} finally {
			IOUtils.closeQuietly(bis);
		}
    }
    
    private com.leansoft.nano.person.PersonsType getNanoPersons(int numberOfRecords) {
    	com.leansoft.nano.person.PersonsType persons = new com.leansoft.nano.person.PersonsType();
    	persons.setPerson(new ArrayList<com.leansoft.nano.person.PersonType>());
		PodamFactory factory = new PodamFactoryImpl();
		for (int i = 0; i < numberOfRecords; i++) {
			persons.getPerson().add(factory.manufacturePojo(com.leansoft.nano.person.PersonType.class));
		}
		return persons;
    }
    
    private void nanoXmlMarshall(int numberOfRecords, String fileName) throws Exception {
    	com.leansoft.nano.person.PersonsType persons = this.getNanoPersons(numberOfRecords);
    	
    	
		File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file), 4096);

		long start = System.currentTimeMillis();
		
		try {
			nanoXmlWriter.write(persons, bos);
			bos.flush();

			long end = System.currentTimeMillis();

			LOG.info("Nano Xml Marshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));
		} finally {
			IOUtils.closeQuietly(bos);
		}
    }
    
    private void nanoXmlUnmarshall(String fileName) throws Exception {
    	File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
    	
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		
		long start = System.currentTimeMillis();
		
		try {
			com.leansoft.nano.person.PersonsType persons = nanoXmlReader.read(com.leansoft.nano.person.PersonsType.class, bis);

			int numberOfRecords = persons.getPerson().size();

			long end = System.currentTimeMillis();

			LOG.info("Nano Xml Unmarshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));

		} finally {
			IOUtils.closeQuietly(bis);
		}
    }
    
    private void nanoJsonMarshall(int numberOfRecords, String fileName) throws Exception {
    	com.leansoft.nano.person.PersonsType persons = this.getNanoPersons(numberOfRecords);
    	
    	
		File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file), 4096);

		long start = System.currentTimeMillis();
		
		try {
			nanoJsonWriter.write(persons, bos);
			bos.flush();

			long end = System.currentTimeMillis();

			LOG.info("Nano Json Marshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));
		} finally {
			IOUtils.closeQuietly(bos);
		}
    }
    
    private void nanoJsonUnmarshall(String fileName) throws Exception {
    	File file = new File( OUTPUT_FOLDER + File.separatorChar
				+ fileName);
    	
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		
		long start = System.currentTimeMillis();
		
		try {
			com.leansoft.nano.person.PersonsType persons = nanoJsonReader.read(com.leansoft.nano.person.PersonsType.class, bis);

			int numberOfRecords = persons.getPerson().size();

			long end = System.currentTimeMillis();

			LOG.info("Nano Json Unmarshall (" + numberOfRecords + "): Time taken in ms: "
					+ (end - start));

		} finally {
			IOUtils.closeQuietly(bis);
		}
    }
}
