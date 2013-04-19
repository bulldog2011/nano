package com.leansoft.nano.ws;

/**
 * Interface for SOAP service response callback,
 * must be implemented and registered for every service call.
 * 
 * @author bulldog
 *
 * @param <R> target type of response, at runtime, Nano will bind the response message with an instance of this type.
 */
public interface SOAPServiceCallback<R> extends XMLServiceCallback<R> {
	
	/**
	 * SOAP fault callback, will be called if the service response containing a SOAP fault,
	 * depends the SOAP version used, you are responsible to cast the generic soapFault object to a concrete one and handle it accordingly.
	 * 
	 * @param soapFault a generic SOAP fault object, must be cast to a concrete SOAP fault, either SOAP 1.1 fault or SOAP 1.2 fault.
	 */
	public void onSOAPFault(Object soapFault);

}
