package com.leansoft.nano.ws;

public interface ServiceCallback<R> {
	
	public void onSuccess(R responseObject);
	
	public void onFailure(Throwable error, String errorMessage);

}
