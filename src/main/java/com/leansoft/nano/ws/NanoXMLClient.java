package com.leansoft.nano.ws;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.leansoft.nano.Format;
import com.leansoft.nano.exception.MarshallException;
import com.leansoft.nano.impl.XmlPullWriter;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Nano async client supporting XML Messaging.
 * 
 * @author bulldog
 *
 */
public abstract class NanoXMLClient {
	
	private static final String TAG = NanoXMLClient.class.getSimpleName();
	
	private String contentType = "text/xml";
	private String endpointUrl = null;
	
	private String charset = "UTF-8";
	
	private boolean debug = false;
	
	private AsyncHttpClient asyncHttpClient = null;
	
    private final Map<String, String> urlParams = new HashMap<String, String>();
	
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
			
			String urlWithQueryString = NanoXMLClient.getUrlWithQueryString(endpointUrl, urlParams);
			
			if (debug) {
				ALog.d(TAG, "Sending request to : " + urlWithQueryString);
				ALog.d(TAG, "Request HTTP headers : ");
				String httpHeaders = MapPrettyPrinter.printMap(asyncHttpClient.getHeaders());
				ALog.d(TAG, httpHeaders);
				ALog.d(TAG, "Request message : ");
				ALog.debugLongMessage(TAG, xmlMessage);
			}
			
			asyncHttpClient.post(null, urlWithQueryString, null, xmlEntiry, contentType, xmlHttpResponseHandler);
			
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
	
	public static String getUrlWithQueryString(String url, Map<String, String> urlParams) {
		String urlWithQueryString = url;
        if(urlParams != null && !urlParams.isEmpty()) {
        	
            List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

            for(Map.Entry<String, String> entry : urlParams.entrySet()) {
                lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        	
            String paramString = URLEncodedUtils.format(lparams, "UTF-8");
            if (url.indexOf("?") == -1) {
            	urlWithQueryString += "?" + paramString;
            } else {
            	urlWithQueryString += "&" + paramString;
            }
        }

        return urlWithQueryString;
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

	/**
	 * Get inner async http client
	 * 
	 * @return inner http client
	 */
	public AsyncHttpClient getAsyncHttpClient() {
		return asyncHttpClient;
	}

	/**
	 * Get current http content type setting
	 * 
	 * @return http content type setting
	 */
	public String getContentType() {
		return contentType;
	}


	/**
	 * Set http content type
	 * 
	 * @param contentType http content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	/**
	 * Get current target endpoint url
	 * 
	 * @return target endpoing url
	 */
	public String getEndpointUrl() {
		return endpointUrl;
	}


	/**
	 * Set target endpoint url
	 * 
	 * @param endpointUrl target endpoint url
	 */
	public void setEndpointUrl(String endpointUrl) {
		if (endpointUrl != null) {
			this.endpointUrl = endpointUrl;
		}
	}

	/**
	 * Get current charset setting for message encoding
	 * 
	 * @return charset for message encoding
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Set charset for message encoding
	 * 
	 * @param charset for message encoding
	 */
	public void setCharset(String charset) {
		if (charset != null) {
			this.charset = charset;
		}
	}

	/**
	 * Get current debug setting
	 * 
	 * @return debug setting
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enable or disable message debugging or logging
	 * 
	 * @param debug true to enable message debugging, false to disable message debugging.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Add additional url parameters, will be appended to url as query string
	 * 
	 * @param name param name
	 * @param value param value
	 */
	public void addUrlParam(String name, String value) {
		urlParams.put(name, value);
	}
	
	/**
	 * Get current additional url parameters setting
	 * 
	 * @return a map of url parameter setting
	 */
	public Map<String, String> getUrlParams() {
		return new HashMap<String, String>(urlParams);
	}
}
