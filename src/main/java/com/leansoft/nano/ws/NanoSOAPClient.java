package com.leansoft.nano.ws;

import org.apache.http.entity.StringEntity;

import com.leansoft.nano.Format;
import com.leansoft.nano.exception.MarshallException;
import com.leansoft.nano.impl.SOAPWriter;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.leansoft.nano.ws.SoapQueryHandler;

import com.loopj.android.http.AsyncHttpClient;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nano async client supporting SOAP Messaging.
 * 
 * @author bulldog
 *
 */
public abstract class NanoSOAPClient {
	
	private static final String TAG = NanoSOAPClient.class.getSimpleName();
	
	private String contentType = "text/xml";
	private String endpointUrl = null;
	
	private String charset = "UTF-8";
	
	private boolean debug = false;
	
	private SOAPVersion soapVersion = SOAPVersion.SOAP11;
	
	private List<Object> customSOAPHeaders = null;
	
	private AsyncHttpClient asyncHttpClient = null;
   
   private Context context = null;
   private SoapQueryHandler soapHandler;
	
    private final Map<String, String> urlParams = new HashMap<String, String>();
	
	public NanoSOAPClient() {
		asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.addHeader("Accept", "text/xml");
	}
   
   public void setContext(Context context)
   {
      this.context = context;
   }
   
   public void setSoapQueryHandler(SoapQueryHandler soapHandler)
   {
      this.soapHandler = soapHandler;
   }
   
   public SoapQueryHandler getSoapQueryHandler(SoapQueryHandler soapHandler)
   {
      return soapHandler;
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
			
			SOAPHttpResponseHandler soapHttpResponseHandler = new SOAPHttpResponseHandler(soapHandler, callback, bindClazz, soapVersion);
			soapHttpResponseHandler.setCharset(charset);
			soapHttpResponseHandler.setDebug(debug);
			
			String urlWithQueryString = NanoXMLClient.getUrlWithQueryString(endpointUrl, urlParams);
			
         if (soapHandler != null)
         {
            soapHandler.handleRequest(urlWithQueryString, MapPrettyPrinter.printMap(asyncHttpClient.getHeaders()), soapMessage);
         }
         
			if (debug) {
				ALog.d(TAG, "Sending request to : " + urlWithQueryString);
				ALog.d(TAG, "Request HTTP headers : ");
				String httpHeaders = MapPrettyPrinter.printMap(asyncHttpClient.getHeaders());
				ALog.d(TAG, httpHeaders);
				ALog.d(TAG, "Request message : ");
				ALog.debugLongMessage(TAG, soapMessage);
			}
			
			asyncHttpClient.post(context, urlWithQueryString, null, soapEntiry, contentType, soapHttpResponseHandler);
			
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
			if (soapVersion == SOAPVersion.SOAP11) {
				com.leansoft.nano.soap11.Envelope envelope = new com.leansoft.nano.soap11.Envelope();
				if (this.customSOAPHeaders != null && this.customSOAPHeaders.size() > 0) {
					com.leansoft.nano.soap11.Header header = new com.leansoft.nano.soap11.Header();
					header.any = this.customSOAPHeaders;
					envelope.header = header;
				}
				envelope.body = new com.leansoft.nano.soap11.Body();
				envelope.body.any = new ArrayList<Object>();
				envelope.body.any.add(requestObject);
				String soapMessage = soapWriter.write(envelope);
				return soapMessage;
			} else {
				com.leansoft.nano.soap12.Envelope envelope = new com.leansoft.nano.soap12.Envelope();
				if (this.customSOAPHeaders != null && this.customSOAPHeaders.size() > 0) {
					com.leansoft.nano.soap12.Header header = new com.leansoft.nano.soap12.Header();
					header.any = this.customSOAPHeaders;
					envelope.header = header;
				}
				envelope.body = new com.leansoft.nano.soap12.Body();
				envelope.body.any = new ArrayList<Object>();
				envelope.body.any.add(requestObject);
				String soapMessage = soapWriter.write(envelope);
				return soapMessage;
			}
			
		} catch (Exception e) {
			throw new MarshallException("fail to convert object of type : " + requestObject.getClass().getName() + " to soap message");
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
	 * Get current content type setting
	 * 
	 * @return content type string
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
	 * Get current endpoint url setting
	 * 
	 * @return endpoint url
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
	 * @return charset charset for message encoding
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Set charset for message encoding
	 * 
	 * @param charset charset for message encoding
	 */
	public void setCharset(String charset) {
		if (charset != null) {
			this.charset = charset;
		}
	}

	/**
	 * Get current debug setting
	 * 
	 * @return current debug setting
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enable or disable message debugging or logging
	 * 
	 * @param debug true to enable debug, false to disable debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Get current SOAP version settting
	 * 
	 * @return current SOAP version
	 */
	public SOAPVersion getSoapVersion() {
		return soapVersion;
	}

	/**
	 * Set SOAP version to use
	 * 
	 * @param soapVersion SOAP version to use
	 */
	public void setSoapVersion(SOAPVersion soapVersion) {
		if (soapVersion != null) {
			this.soapVersion = soapVersion;
		}
	}

	/**
	 * Get current custom SOAP headers setting
	 * 
	 * @return a list of custom SOAP headers
	 */
	public List<Object> getCustomSOAPHeaders() {
		return customSOAPHeaders;
	}

	/**
	 * Set a list of custom SOAP headers,
	 * parameter type in the list can be either Nano bindable class or DOM element.
	 * 
	 * @param customSOAPHeaders a list of custom SOAP headers
	 */
	public void setCustomSOAPHeaders(List<Object> customSOAPHeaders) {
		this.customSOAPHeaders = customSOAPHeaders;
	}
	
	/**
	 * Add url parameter, will be appended to the url as query string
	 * 
	 * @param name param name
	 * @param value param value
	 */
	public void addUrlParam(String name, String value) {
		urlParams.put(name, value);
	}
	
	/**
	 * Get current url parameters setting
	 * 
	 * @return a map of url param setting
	 */
	public Map<String, String> getUrlParams() {
		return new HashMap<String, String>(urlParams);
	}
}
