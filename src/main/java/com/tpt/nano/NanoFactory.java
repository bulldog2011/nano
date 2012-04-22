package com.tpt.nano;

import com.tpt.nano.impl.JsonReader;
import com.tpt.nano.impl.JsonWriter;
import com.tpt.nano.impl.NVReader;
import com.tpt.nano.impl.NVWriter;
import com.tpt.nano.impl.XmlPullWriter;
import com.tpt.nano.impl.XmlSAXReader;

/**
 * Factory class to get IReader/IWriter instance,
 * main entry of the Nano framework.
 * 
 * @author bulldog
 *
 */
public class NanoFactory {
	
	/**
	 * Get IReader instance with default format(encoding is utf-8),
	 * the IReader instance can be used to read XML into Java POJO.
	 * 
	 * @return an instance of IReader implementation
	 */
	public static IReader getXMLReader() {
		return new XmlSAXReader();
	}
	
	/**
     * Get IReader instance with specific format,
	 * the IReader instance can be used to read XML into Java POJO.
	 * 
	 * @param format info about encoding and indent
	 * @return an instance of IReader implementation
	 */
	public static IReader getXMLReader(Format format) {
		return new XmlSAXReader(format);
	}
	
	/**
	 * Get IReader instance with default format(encoding is utf-8),
	 * the IReader instance cna be used to read JSON into POJO.
	 * 
	 * @return an instance of IReader implementation
	 */
	public static IReader getJSONReader() {
		return new JsonReader();
	}
	
	/**
	 * Get IReader instance with specific format,
	 * the IReader instance can be used to read JSON into POJO.
	 * 
	 * @param format info about encoding
	 * @return an instance of IReader implementation.
	 */
	public static IReader getJSONReader(Format format) {
		return new JsonReader(format);
	}
	
	/**
	 * Get IWriter instance with default format(encoding is utf-8, indent is true),
	 * the IWriter instance can be used to write Java POJO into XML.
	 * 
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getXMLWriter() {
		return new XmlPullWriter();
	}
	
	/**
	 * Get IWriter instance with default specific format,
	 * the IWriter instance can be used to write Java POJO into XML.
	 * 
	 * @param format info about encoding and indent
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getXMLWriter(Format format) {
		return new XmlPullWriter(format);
	}
	
	/**
	 * Get IWriter instance with default format(encoding is utf-8),
	 * the IWriter instance can be used to write Java POJO into JSON.
	 * 
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getJSONWriter() {
		return new JsonWriter();
	}

	/**
	 * Get IWriter instance with specific format,
	 * the IWriter instance can be used to write Java POJO into JSON.
	 * 
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getJSONWriter(Format format) {
		return new JsonWriter(format);
	}
	
	/**
	 * Get IWriter instance with default format(encoding is utf-8),
	 * the IWriter instance can be used to write Java POJO into name value pair style query string.
	 * 
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getNVWriter() {
		return new NVWriter();
	}
	
	/**
	 * Get IWriter instance with specific format,
	 * the IWriter instance can be used to write Java POJO into name value pair style query string.
	 * 
	 * @return an instance of IWriter implementation
	 */
	public static IWriter getNVWriter(Format format) {
		return new NVWriter(format);
	}

	/**
	 * Get IReader instance with default format(encoding is utf-8),
	 * the IReader instance can be used to read name value pair style query string into POJO.
	 * 
	 * @return an instance of IReader implementation
	 */
	public static IReader getNVReader() {
		return new NVReader();
	}
	
	/**
	 * Get IReader instance with specific format,
	 * the IReader instance can be used to read name value pair style query string into POJO.
	 * 
	 * @param format info about encoding
	 * @return an instance of IReader implementation.
	 */
	public static IReader getNVReader(Format format) {
		return new NVReader(format);
	}
}
