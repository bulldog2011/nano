package com.tpt.nano;

import java.io.InputStream;
import java.io.Reader;

/**
 * IReader read/de-serialize XML or JSON into POJO.
 * 
 * @author bulldog
 *
 */
public interface IReader {
	
	/**
	 * Read or de-serialize an input stream into a POJO of specific type.
	 * 
	 * @param type target type
	 * @param source an input stream
	 * @return POJO of target type
	 * @throws ReaderException if reads fail
	 */
	public <T> T read(Class<? extends T>type, InputStream source) throws ReaderException, MappingException;
	
	/**
	 * Read or de-serialize an string into a POJO of specific type. 
	 * 
	 * @param type target type
	 * @param source a string
	 * @return POJO of target type
	 * @throws ReaderException if reads fail
	 */
	public <T> T read(Class<? extends T>type, String source) throws ReaderException, MappingException;
	
	/**
	 * Read or de-serialize a character stream reader into a POJO of specific type.
	 * 
	 * @param type target type
	 * @param source a character stream reader
	 * @return POJO of target type
	 * @throws ReaderException if reads fail
	 */
	public <T> T read(Class<? extends T>type, Reader source) throws ReaderException, MappingException;

}
