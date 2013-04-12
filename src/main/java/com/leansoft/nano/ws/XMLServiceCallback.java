package com.leansoft.nano.ws;

public interface XMLServiceCallback<R> {
	
	public void onSuccess(R responseObject);
	
	public void onFailure(Throwable error, String errorMessage);

}
