package com.leansoft.nano.impl;

import java.io.Writer;

import org.xmlpull.v1.XmlSerializer;

import com.leansoft.nano.Format;
import com.leansoft.nano.annotation.schema.RootElementSchema;
import com.leansoft.nano.exception.MappingException;
import com.leansoft.nano.exception.WriterException;
import com.leansoft.nano.util.StringUtil;

public class SOAPWriter extends XmlPullWriter {
	
	static final String SOAP_PREFIX = "soapenv";
	static final String XSI_PREFIX = "xsi";
	static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	static final String XSD_PREFIX = "xsd";
	static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	
	public SOAPWriter() {
		super();
	}
	
	public SOAPWriter(Format format) {
		super(format);
	}
	
	
	@Override
	public void write(Object source, Writer out) throws WriterException, MappingException {
		try {
			// entry validation
			validate(source, out);
			
			if (!(source instanceof com.leansoft.nano.soap11.Envelope) && !(source instanceof com.leansoft.nano.soap12.Envelope)) {
				throw new IllegalArgumentException("Can't write no-soap object of type : " + source.getClass().getName());
			}
	
			XmlSerializer serializer = factory.newSerializer();
			if (format.isIndent()) {
				try {
					serializer.setFeature(IDENT_PROPERTY, true);
				} catch (IllegalStateException ise) {
					serializer.setProperty(PROPERTY_SERIALIZER_INDENTATION, "    ");
				}
			}
			serializer.setOutput(out);
			serializer.startDocument(format.getEncoding(), null);
			
			MappingSchema ms = MappingSchema.fromObject(source);
			RootElementSchema res = ms.getRootElementSchema();
			String namespace = res.getNamespace();
			String xmlName = res.getXmlName();
			
			// set soap prefix
			serializer.setPrefix(SOAP_PREFIX, namespace);
			serializer.setPrefix(XSI_PREFIX, XSI_NAMESPACE);
			serializer.setPrefix(XSD_PREFIX, XSD_NAMESPACE);
			
			// set default namespace without prefix
			String innerNamespace = this.findInnerClassNamespace(source);
			if (!StringUtil.isEmpty(innerNamespace)) {
				if (serializer.getPrefix(innerNamespace, false) == null) {
					serializer.setPrefix("", innerNamespace);
				}
			}
			
			serializer.startTag(namespace, xmlName);
			this.writeObject(serializer, source, namespace);
			serializer.endTag(namespace, xmlName);
			
			serializer.endDocument();
		} catch (MappingException me) {
			throw me;
		} catch (IllegalArgumentException iae) {
			throw new WriterException("Entry validation failure", iae);
		} catch (Exception e) {
			throw new WriterException("Error to write/serialize object", e);
		}
	}
	
	private String findInnerClassNamespace(Object obj) throws MappingException  {
		Object innerObject = null;
		if (obj instanceof com.leansoft.nano.soap11.Envelope) {
			com.leansoft.nano.soap11.Envelope envelope = (com.leansoft.nano.soap11.Envelope)obj;
			com.leansoft.nano.soap11.Body body = envelope.body;
			if (body != null && body.any != null && body.any.size() > 0) {
				innerObject = body.any.get(0);
			}
		} else if (obj instanceof com.leansoft.nano.soap12.Envelope) {
			com.leansoft.nano.soap12.Envelope envelope = (com.leansoft.nano.soap12.Envelope)obj;
			com.leansoft.nano.soap12.Body body = envelope.body;
			if (body != null && body.any != null && body.any.size() > 0) {
				innerObject = body.any.get(0);
			}
		}
		
		if (innerObject != null) {
			MappingSchema ms = MappingSchema.fromObject(innerObject);
			RootElementSchema res = ms.getRootElementSchema();
			
			return res.getNamespace();
		}
		
		return null;
	}

}
