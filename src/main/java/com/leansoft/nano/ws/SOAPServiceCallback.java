package com.leansoft.nano.ws;

public interface SOAPServiceCallback<R> extends XMLServiceCallback<R> {
	
	public void onSOAPFault(Object soapFault);

}
