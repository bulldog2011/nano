##_DISCLAIMER_
#### This library is still under construction. Beta testers are more than welcome.

# Nano

A light Web Service client framework targeting Android platform

##Feature Highlight
1. Support WSDL driven development, [code generator](https://github.com/bulldog2011/mwsc) tool is provided to auto-genearte strongly typed proxy from WSDL. 
2. Support SOAP 1.1/1.2 and XML based web service. 
3. Support automatic SOAP/XML to Java object binding, performance is comparable to Android native XML parser.
4. Built on popular and mature [loopj async http client](https://github.com/loopj/android-async-http) library for Android.
5. Has been verified with industrial grade Web Service like Amazon ECommerce Web Serivce and eBay Finding/Shopping/Trading Web Service. 
6. Support asynchronous service invocation, flexible HTTP/SOAP header setting, timeout setting, encoding setting, logging, etc.
7. Light-weight, the library jar is less than 150K, no external dependencies on Android platform.
8. Besides Web Service, can also be used as a standalone XML and JSON binding framework.

## The Big Picture
![The Big Picture](http://bulldog2011.github.com/images/nano/big_picture.png)

##How to Use
You have a few options:

1. Direct jar reference  
Download latest [0.7.0 release](https://github.com/bulldog2011/bulldog-repo/tree/master/repo/releases/com/leansoft/nano/0.7.0)  

2. Include the whole source of Nano into your project

3. Maven reference

``` xml
	<dependency>
	  <groupId>com.leansoft</groupId>
	  <artifactId>nano</artifactId>
	  <version>0.7.0</version>
	</dependency>
	
	<repository>
	  <id>github.release.repo</id>
	  <url>https://raw.github.com/bulldog2011/bulldog-repo/master/repo/releases/</url>
	</repository>
```

After including Nano into your project, please make sure to add following user permissions in the `AndroidManifest.xml` file for network access:

``` xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

##WSDL Driven Development Flow
1. Generate Java proxy from WSDL
2. Create new Android project, add Nano runtime and generated proxy into your project
3. Implement appliction logic and UI, call proxy to invoke web service as needed.

##Example Usage
After the service proxy is generated from wsdl, service invocation through Nano runtime is extremely simple:

``` java

	// Get shared client
	NumberConversionSoapType_SOAPClient client = NumberConversionServiceClient.getSharedClient();
	client.setDebug(true); // enable soap message logging
	
	// build request
	NumberToWords request = new NumberToWords();
	try {
		String number = ((EditText)findViewById(R.id.numerInputText)).getText().toString();
		request.ubiNum = new BigInteger(number);
	} catch (NumberFormatException ex) {
		Toast.makeText(MainActivity.this, "Invalid integer number", Toast.LENGTH_LONG).show();
		return;
	}
	
	client.numberToWords(request, new SOAPServiceCallback<NumberToWordsResponse>() {

		@Override
		public void onSuccess(NumberToWordsResponse responseObject) { // success
			Toast.makeText(MainActivity.this, responseObject.numberToWordsResult, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFailure(Throwable error, String errorMessage) { // http or parsing error
			Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onSOAPFault(Object soapFault) { // soap fault
			Fault fault = (Fault)soapFault;
			Toast.makeText(MainActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
		}
		
	});

```

## Web Service Sample List
All samples are in the [sample](https://github.com/bulldog2011/nano/tree/master/sample/webservice) folder, following samples are included:

* NumberConverter - sample using [Number Conversion Service](http://www.dataaccess.com/webservicesserver/numberconversion.wso) SOAP web service from [Data Access Worldwide](http://www.dataaccess.com/).
* USZipValidator - sample using [US Zip Validation Service](http://www.webservicemart.com/uszip.asmx) SOAP Web service from [WebServiceMart](http://www.webservicemart.com/)
* BarCode - Demo app using [BarcodeGenerator](http://www.webservicex.net/ws/WSDetails.aspx?CATID=8&WSID=76) SOAP web serivce from webserviceX.NET
* HelloAmazonProductAdvertising - Hello world like sample using [Amazon Product Advertising API](https://affiliate-program.amazon.com/gp/advertising/api/detail/main.html) SOAP call.
* HelloeBayFinding - Hello world like sample using [eBay Finding API](https://www.x.com/developers/ebay/products/finding-api) SOAP call.
* HelloeBayShopping - Hello world like sample using [eBay Shopping API](https://www.x.com/developers/ebay/products/shopping-api) XML call.
* AmazonBookFinder - Sample Amazon Book search and purchase app using Amazon Product Advertising API.
* eBayDemoApp - Sample eBay Search App using both eBay Finding API and eBay Shopping API.



##Docs for Web Service
1. [WSDL Driven Development on Android - the Big Picture](http://bulldog2011.github.io/blog/2013/04/15/wsdl-driven-development-on-android-the-big-picture/)
2. [Nano Tutorial 1 - a Number Conversion Sample](http://bulldog2011.github.io/blog/2013/04/15/nano-tutorial-1-a-number-conversion-sample/)
3. [Nano Tutorial 2 - a BarCode Generator Sample](http://bulldog2011.github.io/blog/2013/04/17/nano-tutorial-2-a-barcode-sample/)

##Docs for Binding
1. [Nano Hello World](http://bulldog2011.github.com/blog/2013/02/05/nano-hello-world/)
2. [Nano List Handling](http://bulldog2011.github.com/blog/2013/02/05/nano-list-tutorial/)
3. [Nano Compare to JAXB](http://bulldog2011.github.com/blog/2013/02/06/nano-compare-to-jaxb/)
4. [Scheam driven data binding with Nano and mxjc](http://bulldog2011.github.com/blog/2013/02/07/schema-driven-nano-binding/)
5. [Xml Parser and Nano Benchmark on Android](http://bulldog2011.github.com/blog/2013/02/08/nano-benchmark-on-android/)
6. [Nano on Android Tutorial 1](http://bulldog2011.github.com/blog/2013/02/10/nano-on-android-tutorial-1/)
7. [A full movie search Android application using Nano binding](http://bulldog2011.github.com/blog/2013/02/12/movie-search-android-app-using-nano/)
8. [Schema Driven Web Serivce Client Development on Android, Part 1 : Hello eBay Finding](http://bulldog2011.github.com/blog/2013/02/17/schema-driven-on-android-part-1-hello-ebay-finding/)
9. [Schema Driven Web Serivce Client Development on Android, Part 2 : eBay Search App](http://bulldog2011.github.com/blog/2013/02/19/schema-driven-on-android-part-2-ebay-search/)


##Mapping between XML Schema Types and Java Types 

<table>
<tr><th>XML Schema Data Types</th><th>Objective-C Data Types</th></tr>
<tr>
    <td>xsd:base64Binary</td>
    <td>byte[]</td>
</tr>
<tr>
    <td>xsd:boolean</td>
    <td>boolean</td>
</tr>
<tr>
    <td>xsd:byte</td>
    <td>byte</td>
</tr>
<tr>
    <td>xsd:date</td>
    <td>java.util.Date</td>
</tr>
<tr>
    <td>xsd:dateTime</td>
    <td>java.util.Date</td>
</tr>
<tr>
    <td>xsd:decimal</td>
    <td>java.math.BigDecimal</td>
</tr>
<tr>
    <td>xsd:double</td>
    <td>double</td>
</tr>
<tr>
    <td>xsd:duration</td>
    <td>com.leansoft.nano.custom.types.Duration</td>
</tr>
<tr>
    <td>xsd:float</td>
    <td>float</td>
</tr>
<tr>
    <td>xsd:g</td>
    <td>java.util.Date</td>
</tr>
<tr>
    <td>xsd:hexBinary</td>
    <td>byte[]</td>
</tr>
<tr>
    <td>xsd:int</td>
    <td>int</td>
</tr>
<tr>
    <td>xsd:integer</td>
    <td>java.lang.BigInteger</td>
</tr>
<tr>
    <td>xsd:long</td>
    <td>long</td>
</tr>
<tr>
    <td>xsd:NOTATION</td>
    <td>javax.xml.namespace.QName</td>
</tr>
<tr>
    <td>xsd:Qname</td>
    <td>javax.xml.namespace.QName</td>
</tr>
<tr>
    <td>xsd:short</td>
    <td>short</td>
</tr>
<tr>
    <td>xsd:string</td>
    <td>java.lang.String</td>
</tr>
<tr>
    <td>xsd:time</td>
    <td>java.util.Date</td>
</tr>
<tr>
    <td>xsd:unsignedByte</td>
    <td>short</td>
</tr>
<tr>
    <td>xsd:unsignedInt</td>
    <td>long</td>
</tr>
<tr>
    <td>xsd:unsignedShort</td>
    <td>int</td>
</tr>
</table>

## Version History

#### 0.7.0 - April 14, 2013 [repository](https://github.com/bulldog2011/bulldog-repo/tree/master/repo/releases/com/leansoft/nano/0.7.0)
  * Initial release supporting SOAP/XML Web Service.


##Compatibility
Nano has been verified with Android 2.2(API 8) and 2.3.6(API 10), Nano should work without problem on Android 2.2 and above although this hasn't been verified formally.


##Current Limitation
1. Only Document/Literal style Web Service is support, RPC style Web Serivice is not supported.
2. SOAP attachment is not supported


###Copyright and License
Copyright 2012 LeanSoft, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
