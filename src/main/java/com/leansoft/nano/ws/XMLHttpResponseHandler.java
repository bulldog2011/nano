package com.leansoft.nano.ws;

import java.io.IOException;
import java.util.HashMap;
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
import com.leansoft.nano.impl.XmlDOMReader;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.util.MapPrettyPrinter;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class XMLHttpResponseHandler extends AsyncHttpResponseHandler {
	
    private static final String TAG = XMLHttpResponseHandler.class.getSimpleName();
	
    protected static final int SUCCESS_RESPONSE_HANDLING_MESSAGE = 100;
	
	@SuppressWarnings("rawtypes")
	private ServiceCallback callback;
	private Class<?> bindClazz;
	private String charset;
	private boolean debug;
	
	@SuppressWarnings("rawtypes")
	public XMLHttpResponseHandler(ServiceCallback callback, Class<?> bindClazz) {
		super();
		this.callback = callback;
		this.bindClazz = bindClazz;
	}
	
	@Override
    public void onFailure(Throwable error, String content) {
        this.callback.onFailure(error, content);
    }
	
	
	@Override
    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
		try {
			// unmarshalling
			Object responseObject = this.convertXMLToObject(responseBody);
			if (responseObject != null) {
		        sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObject}));
			} else {
				throw new UnmarshallException("Fail to convert XML response to object of type :" + bindClazz.getName());
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
					Object responseObject = this.convertXMLToObject(responseBody);
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
	
	private Object convertXMLToObject(String responseContent) throws UnmarshallException {
		
		try {
			Format format = new Format(true, charset);
			XmlDOMReader xmlReader = new XmlDOMReader(format);
			return xmlReader.read(bindClazz, responseContent);
		
		} catch (Exception e) {
			throw new UnmarshallException("Fail to convert XML response to object of type :" + bindClazz.getName(), e);
		}
	}
	
    @SuppressWarnings("unchecked")
	protected void handleSuccessResponse(Object responseObject) {
    	
		callback.onSuccess(responseObject);
    	
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
