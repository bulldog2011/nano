package com.leansoft.nano.ws;

import java.io.IOException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Message;

import com.leansoft.nano.Format;
import com.leansoft.nano.exception.UnmarshallException;
import com.leansoft.nano.impl.SOAPReader;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;

public class SOAPHttpResponseHandler extends AsyncHttpResponseHandler {
	
	private static final String TAG = SOAPHttpResponseHandler.class.getSimpleName();
	
    protected static final int SUCCESS_RESPONSE_HANDLING_MESSAGE = 100;
	
	@SuppressWarnings("rawtypes")
	private SOAPServiceCallback callback;
	private Class<?> bindClazz;
	private SOAPVersion soapVersion;
	private String charset;
	private boolean debug;
	
	@SuppressWarnings("rawtypes")
	public SOAPHttpResponseHandler(SOAPServiceCallback callback, Class<?> bindClazz, SOAPVersion soapVersion) {
		super();
		this.callback = callback;
		this.bindClazz = bindClazz;
		this.soapVersion = soapVersion;
	}
	
	@Override
    public void onFailure(Throwable error, String content) {
        this.callback.onFailure(error, content);
    }
	
	
	@Override
    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
		try {
			// unmarshalling
			Object responseObject = this.convertSOAPToObject(responseBody);
			if (responseObject != null) {
		        sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObject}));
			} else {
				throw new UnmarshallException("Fail to convert SOAP response to object of type :" + bindClazz.getName());
			}
			
		} catch (UnmarshallException e) {
			ALog.e(TAG, "Response unmarshalling exception", e);
			super.sendFailureMessage(e, "Response unmarshalling exception");
		}
    }
	
    protected void sendFailureMessage(Throwable e, String responseBody) {
    	if (e instanceof HttpResponseException) {
    		HttpResponseException httpResponseException = (HttpResponseException)e;
    		if (httpResponseException.getStatusCode() >= 300) {// may be still a successful response
    			try {
					Object responseObject = this.convertSOAPToObject(responseBody);
					if (responseObject != null) {
				        sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObject}));
				        return;
					}
				} catch (UnmarshallException e1) {
					// ignore
				}
    		}
    	}
		ALog.e(TAG, responseBody, e);
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }
	
	private Object convertSOAPToObject(String responseContent) throws UnmarshallException {
		
		try {
			Format format = new Format(true, charset);
			SOAPReader soapReader = new SOAPReader(format);
			if (soapVersion == SOAPVersion.SOAP11) {
				com.leansoft.nano.soap11.Envelope envelope = soapReader.read(com.leansoft.nano.soap11.Envelope.class, bindClazz, responseContent);
				
				if (envelope != null && envelope.body != null && envelope.body.any != null && envelope.body.any.size() > 0) {
					return envelope.body.any.get(0);
				}
				
			} else {
				com.leansoft.nano.soap12.Envelope envelope = soapReader.read(com.leansoft.nano.soap12.Envelope.class, bindClazz, responseContent);
				if (envelope != null && envelope.body != null && envelope.body.any != null && envelope.body.any.size() > 0) {
					return envelope.body.any.get(0);
				}
			}
		
		} catch (Exception e) {
			throw new UnmarshallException("Fail to convert SOAP response to object of type :" + bindClazz.getName(), e);
		}
		
		return null;
	}
	
    @SuppressWarnings("unchecked")
	protected void handleSuccessResponse(Object responseObject) {
    	
    	if (responseObject instanceof com.leansoft.nano.soap11.Fault || responseObject instanceof com.leansoft.nano.soap12.Fault) {
    		callback.onSOAPFault(responseObject);
    	} else {
    		callback.onSuccess(responseObject);
    	}
    	
    }
	
    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;

        switch(msg.what) {
            case SUCCESS_RESPONSE_HANDLING_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessResponse(response[0]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], (String)response[1]);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }
    
    // Interface to AsyncHttpRequest
    protected void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch(IOException e) {
            sendFailureMessage(e, (String) null);
        }
        
		if (debug) {
			ALog.d(TAG, "Response HTTP status : " + status.getStatusCode());
			Map<String, String> headerMap = this.getHeaderMap(response);
			String headers = MapPrettyPrinter.printMap(headerMap);
			ALog.d(TAG, "Response HTTP headers : ");
			ALog.d(TAG, headers);
			ALog.d(TAG, "Response message : ");
			ALog.debugLongMessage(TAG, responseBody);
		}

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
            sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
        }
    }
    
    private Map<String, String> getHeaderMap(HttpResponse response) {
    	Map<String, String> headerMap = new HashMap<String, String>();
    	Header[] headers = response.getAllHeaders();
    	if (headers != null && headers.length > 0) {
    		for(Header header : headers) {
    			headerMap.put(header.getName(), header.getValue());
    		}
    	}
    	return headerMap;
    }

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
