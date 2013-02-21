Nano
========

A super light xml and json binding framework supporting both Java and Android platform.

###Feature Highlight
1. ***Light-Weight*** : the library jar is less than 70K, no external dependencies on Android platform.
2. ***Fast*** : performance comparable to Android native xml parser like SAX parser and XmlPull parser, see benchmark [here](http://bulldog2011.github.com/blog/2013/02/08/nano-benchmark-on-android/).
3. ***Memory-efficient*** : memory usage is minimized by leverging streaming parsing technology.
4. ***Annotation Driven*** : No custom serialization code is needed, just add a few light annotations to make the class bindable.
5. ***Support both XML and JSON*** : object instance can be serialized to or deserialized from either XML or JSON.
6. ***Schema Compiler Provided*** : [compiler tool](https://github.com/bulldog2011/mxjc) to auto generate bindable class from Xml schema or WSDL. 


###How to Use
1. Direct jar reference  
Download latest [0.6.3 release](https://github.com/bulldog2011/bulldog-repo/tree/master/repo/releases/com/leansoft/nano/0.6.3)  
Note:  
    * On normal Java platform, Nano depends on KXml and org.json libraries, you can extract these libraries in the nano release zip package.  
    * On Android plaform, Nano does ***not*** depend on external Kxml and org.json libraries, since these libraries have already been included in Android.   

2. Maven refereence

		<dependency>
		  <groupId>com.leansoft</groupId>
		  <artifactId>nano</artifactId>
		  <version>0.6.3</version>
		</dependency>
		
		<repository>
		  <id>github.release.repo</id>
		  <url>https://raw.github.com/bulldog2011/bulldog-repo/master/repo/releases/</url>
		</repository>

###Docs
1. [Nano Hello World](http://bulldog2011.github.com/blog/2013/02/05/nano-hello-world/)
2. [Nano List Handling](http://bulldog2011.github.com/blog/2013/02/05/nano-list-tutorial/)
3. [Nano Compare to JAXB](http://bulldog2011.github.com/blog/2013/02/06/nano-compare-to-jaxb/)
4. [Scheam driven data binding with Nano and mxjc](http://bulldog2011.github.com/blog/2013/02/07/schema-driven-nano-binding/)
5. [Xml Parser and Nano Benchmark on Android](http://bulldog2011.github.com/blog/2013/02/08/nano-benchmark-on-android/)
6. [Nano on Android Tutorial 1](http://bulldog2011.github.com/blog/2013/02/10/nano-on-android-tutorial-1/)
7. [A full movie search Android application using Nano binding](http://bulldog2011.github.com/blog/2013/02/12/movie-search-android-app-using-nano/)
8. [Schema Driven Web Serivce Client Development on Android, Part 1 : Hello eBay Finding](http://bulldog2011.github.com/blog/2013/02/17/schema-driven-on-android-part-1-hello-ebay-finding/)
9. [Schema Driven Web Serivce Client Development on Android, Part 2 : eBay Search App](http://bulldog2011.github.com/blog/2013/02/19/schema-driven-on-android-part-2-ebay-search/)


###Compatibility
1. On Android, Nano has been verified with Android 1.5(API 3), 1.6(API 4), 2.1(API 7) and 2.2(API 8), Nano should work without problem on Android 2.3 and above although this hasn't been verified formally.
2. On Normal Java, Nano has been verified with Oracle/Sun JDK 1.6.


###Current Limitation
1. For Java collection types, only java.util.List is supported, and at most one type parameter is allowed, java.util.Set and java.util.Map are not supported.
2. Java array is not supported, except byte[] which will be serialized to base64 string.
2. Not all Java primitives or frequently used types are supported, for support list, see [here](https://github.com/bulldog2011/nano/tree/master/src/main/java/com/leansoft/nano/transform)


###Copyright and License
Copyright 2012 LeanSoft, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.