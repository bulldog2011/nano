package com.leansoft.nano.ws;

public interface SOAPServiceCallback<R> extends ServiceCallback<R> {
	
	public void onSOAPFault(Object soapFault);

}
