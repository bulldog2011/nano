/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import com.leansoft.nano.log.ALog;

class AsyncHttpRequest implements Runnable {
	
    private static final String TAG = AsyncHttpRequest.class.getSimpleName();
	
    private final AbstractHttpClient client;
    private final HttpContext context;
    private final HttpUriRequest request;
    private final AsyncHttpResponseHandler responseHandler;

    public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, AsyncHttpResponseHandler responseHandler) {
        this.client = client;
        this.context = context;
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
    	makeRequestWithExceptionHandling();
    }

    private void makeRequest() throws IOException {
        if(!Thread.currentThread().isInterrupted()) {
        	try {
        		HttpResponse response = client.execute(request, context);
        		if(!Thread.currentThread().isInterrupted()) {
        			if(responseHandler != null) {
        				responseHandler.sendResponseMessage(response);
        			}
        		} else{
        			//TODO: should raise InterruptedException? this block is reached whenever the request is cancelled before its response is received
        		}
        	} catch (IOException e) {
        		if(!Thread.currentThread().isInterrupted()) {
        			throw e;
        		}
        	}
        }
    }

    private void makeRequestWithExceptionHandling() {
        try {
        	makeRequest();
        } catch (UnknownHostException e) {
	        if(responseHandler != null) {
	            responseHandler.sendFailureMessage(e, "can't resolve host", null);
	        }
	        ALog.e(TAG, "can't resolve host", e);
        } catch (SocketException e){
            // Added to detect host unreachable
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(e, "can't resolve host", null);
            }
	        ALog.e(TAG, "can't resolve host", e);
        }catch (SocketTimeoutException e){
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(e, "socket time out", null);
            }
	        ALog.e(TAG, "socket time out", e);
        } catch (IOException e) {
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(e, "IO exception - " + e.getMessage(), null);
            }
	        ALog.e(TAG, "IO exception - " + e.getMessage(), e);
        } catch (NullPointerException e) {
            // there's a bug in HttpClient 4.0.x that on some occasions causes
            // DefaultRequestExecutor to throw an NPE, see
            // http://code.google.com/p/android/issues/detail?id=5255
            if(responseHandler != null) {
            	IOException ioe = new IOException("NPE in HttpClient" + e.getMessage());
                responseHandler.sendFailureMessage(ioe, "NPE in HttpClient" + e.getMessage(), null);
            }
	        ALog.e(TAG, "NPE in HttpClient" + e.getMessage(), e);
        } catch (Throwable t) {
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(t, "Connect exception - " + t.getMessage(), null);
            }
	        ALog.e(TAG, "Connect exception - " + t.getMessage(), t);
        }
    }
}
