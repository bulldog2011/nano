package com.leansoft.nano.ws;

import org.apache.http.entity.StringEntity;

import com.leansoft.nano.Format;
import com.leansoft.nano.exception.MarshallException;
import com.leansoft.nano.impl.XmlPullWriter;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.loopj.android.http.AsyncHttpClient;

public abstract class NanoXMLClient {
	
	private static final String TAG = NanoXMLClient.class.getSimpleName();
	
	private String contentType = "text/xml";
	private String endpointUrl = null;
	
	private String charset = "UTF-8";
	
	private boolean debug = false;
	
	private AsyncHttpClient asyncHttpClient = null;
	
	public NanoXMLClient() {
		asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.addHeader("Accept", "text/xml");
	}
	
	protected void invoke(Object requestObject, XMLServiceCallback<?> callback, Class<?> bindClazz) {
		
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
			String xmlMessage = this.convertObjectToXML(requestObject);
			if (xmlMessage == null) {
				throw new MarshallException("fail to convert object of type : " + requestObject.getClass().getName() + " to xml message");
			}
			
			StringEntity xmlEntiry = new StringEntity(xmlMessage, charset);
			
			XMLHttpResponseHandler xmlHttpResponseHandler = new XMLHttpResponseHandler(callback, bindClazz);
			xmlHttpResponseHandler.setCharset(charset);
			xmlHttpResponseHandler.setDebug(debug);
			
			if (debug) {
				ALog.d(TAG, "Sending request to : " + endpointUrl);
				ALog.d(TAG, "Request HTTP headers : ");
				String httpHeaders = MapPrettyPrinter.printMap(asyncHttpClient.getHeaders());
				ALog.d(TAG, httpHeaders);
				ALog.d(TAG, "Request message : ");
				ALog.debugLongMessage(TAG, xmlMessage);
			}
			
			asyncHttpClient.post(null, endpointUrl, null, xmlEntiry, contentType, xmlHttpResponseHandler);
			
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
	
	
	private String convertObjectToXML(Object requestObject) throws MarshallException {
		Format format = new Format(true, charset);
		XmlPullWriter xmlWriter = new XmlPullWriter(format);
		try {
			return xmlWriter.write(requestObject);
		} catch (Exception e) {
			throw new MarshallException("fail to convert object of type : " + requestObject.getClass().getName() + " to xml message");
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
}
