package com.tpt.nano;

/**
 * This exception will be thrown if there is mapping error found by the framework.
 * 
 * @author bulldog
 *
 */
@SuppressWarnings("serial")
public class MappingException extends RuntimeException {

	public MappingException() {
	}

	public MappingException(String arg0) {
		super(arg0);
	}

	public MappingException(Throwable arg0) {
		super(arg0);
	}

	public MappingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
