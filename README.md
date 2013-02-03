Nano
========

A super light xml and json binding framework supporting both Java and Android platform.

###Feature Highlight
1. ***Light-Weight*** : the library jar is less than 70K, no external dependencies on Android platform.
2. ***Fast*** : performance comparable to Android native xml parser like SAX parser and XmlPull parser.
3. ***Memory-efficient*** : memory usage is minimized by leverging streaming parsing technology.
4. ***Annotation Driven*** : No custom serialization code is needed, just add a few light annotations to make the class bindable.
5. ***Support both XML and JSON*** : object instance can be serialized to or deserialized from either XML or JSON.
6. ***Schema Compiler Provided*** : compiler tool is provided to auto generate bindable class from Xml schema or WSDL.



###How to Use
1. Direct jar reference  
Download latest [0.6.1 release](https://github.com/bulldog2011/bulldog-repo/tree/master/repo/releases/com/leansoft/nano/0.6.1)

2. Maven refereence

		<dependency>
		  <groupId>com.leansoft</groupId>
		  <artifactId>nano</artifactId>
		  <version>0.6.1</version>
		</dependency>
		
		<repository>
		  <id>github.release.repo</id>
		  <url>https://raw.github.com/bulldog2011/bulldog-repo/master/repo/releases/</url>
		</repository>






