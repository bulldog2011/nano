package com.leansoft.nano.ws;

public interface SoapQueryHandler
{
   public void handleRequest(String url, String httpHeaders, String soapMessage);
   public void handleResponse(int status, String httpHeaders, String soapMessage);
}