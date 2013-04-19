package com.leansoft.nano.ws;

/**
 * Interface for XML service response callback,
 * must be implemented and registered for every service call.
 * 
 * @author bulldog
 *
 * @param <R> target type of response, at runtime, Nano will bind the response message with an instance of this type.
 */
public interface XMLServiceCallback<R> {
	
	/**
	 * Success callback, will be called if the service call is successful.
	 * 
	 * @param responseObject response object of type R
	 */
	public void onSuccess(R responseObject);
	
	/**
	 * Failure callback, will be called if there is any http or parsing error during the service call.
	 * 
	 * @param error an exception thrown during the service call
	 * @param errorMessage a short error message describing the error
	 */
	public void onFailure(Throwable error, String errorMessage);

}
