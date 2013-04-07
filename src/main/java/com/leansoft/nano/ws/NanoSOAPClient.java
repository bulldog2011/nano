package com.leansoft.nano.ws;

import org.apache.http.entity.StringEntity;

import com.leansoft.nano.Format;
import com.leansoft.nano.exception.MarshallException;
import com.leansoft.nano.impl.SOAPWriter;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.loopj.android.http.AsyncHttpClient;

public abstract class NanoSOAPClient {
	
	private static final String TAG = NanoSOAPClient.class.getSimpleName();
	
	private String contentType = null;
	private String endpointUrl = null;
	
	private String charset = "UTF-8";
	
	private boolean debug = false;
	
	private SOAPVersion soapVersion = SOAPVersion.SOAP11;
	
	private AsyncHttpClient asyncHttpClient = null;
	
	public NanoSOAPClient() {
		asyncHttpClient = new AsyncHttpClient();
	}
	
	protected void invoke(Object requestObject, SOAPServiceCallback<?> callback, Class<?> bindClazz) {
		
		try {
			if (endpointUrl == null) {
				throw new IllegalArgumentException("Endpoint url is missing on client");
			}
			
			if (callback == null) {
				throw new IllegalArgumentException("callback is missing");
			}
			
			if (bindClazz == null) {
				throw new IllegalArgumentException("target bind class is missing");
			}
			
			// marshalling
			String soapMessage = this.convertObjectToSOAP(requestObject);
			if (soapMessage == null) {
				throw new MarshallException("fail to convert object of type : " + requestObject.getClass().getName() + " to soap message");
			}
			
			StringEntity soapEntiry = new StringEntity(soapMessage, charset);
			
			SOAPHttpResponseHandler soapHttpResponseHandler = new SOAPHttpResponseHandler(callback, bindClazz, soapVersion);
			soapHttpResponseHandler.setCharset(charset);
			soapHttpResponseHandler.setDebug(debug);
			
			if (debug) {
				ALog.d(TAG, "Sending request to : " + endpointUrl);
				ALog.d(TAG, "Request HTTP headers : ");
				String httpHeaders = MapPrettyPrinter.printMap(asyncHttpClient.getHeaders());
				ALog.d(TAG, httpHeaders);
				ALog.d(TAG, "Request message : ");
				ALog.debugLongMessage(TAG, soapMessage);
			}
			
			asyncHttpClient.post(null, endpointUrl, null, soapEntiry, contentType, soapHttpResponseHandler);
			
		} catch (Exception e) {
			ALog.e(TAG, "Fail to send request", e);
			if (callback != null) {
				callback.onFailure(e, "Fail to send request");
			} else {
				if (e instanceof RuntimeException) {
					throw (RuntimeException)e;
				} else {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	
	private String convertObjectToSOAP(Object requestObject) throws MarshallException {
		Format format = new Format(true, charset);
		SOAPWriter soapWriter = new SOAPWriter(format);
		try {
			String result = soapWriter.write(requestObject);
			return result;
		} catch (Exception e) {
			throw new MarshallException("fail to convert object of type : " + requestObject.getClass().getName() + " to soap message");
		}
	}

	public AsyncHttpClient getAsyncHttpClient() {
		return asyncHttpClient;
	}

	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getEndpointUrl() {
		return endpointUrl;
	}


	public void setEndpointUrl(String endpointUrl) {
		if (endpointUrl != null) {
			this.endpointUrl = endpointUrl;
		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		if (charset != null) {
			this.charset = charset;
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public SOAPVersion getSoapVersion() {
		return soapVersion;
	}

	public void setSoapVersion(SOAPVersion soapVersion) {
		if (soapVersion != null) {
			this.soapVersion = soapVersion;
		}
	}
}
