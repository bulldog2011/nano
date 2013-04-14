package com.leansoft.nano.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.leansoft.nano.Format;
import com.leansoft.nano.annotation.schema.AnyElementSchema;
import com.leansoft.nano.annotation.schema.RootElementSchema;
import com.leansoft.nano.exception.MappingException;
import com.leansoft.nano.exception.ReaderException;
import com.leansoft.nano.impl.MappingSchema;

public class SOAPReader extends XmlDOMReader {
	
	private static final ThreadLocal<ReadContext> CONTEXT =
		    new ThreadLocal<ReadContext>() {
		        @Override 
		        protected ReadContext initialValue() {
		            return new ReadContext();
		        }
		    };
	
	public SOAPReader() {
		super(new Format());
	}
	
	public SOAPReader(Format format) {
		super(format);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Class<? extends T> soapClazz, Class<?> innerClazz, InputStream source)
			throws ReaderException, MappingException {
		
		super.validate(soapClazz, source);
		
		if (soapClazz != com.leansoft.nano.soap11.Envelope.class && soapClazz != com.leansoft.nano.soap12.Envelope.class) {
			throw new ReaderException("Can't read non-soap class : " + soapClazz.getName());
		}
		
		if (innerClazz == null) {
			throw new ReaderException("Can not read, innerClazz is null!");
		}
		
		try {
			DocumentBuilder db = builderLocal.get();
			Document doc = db.parse(source);
			
			Element rootElement = doc.getDocumentElement();
			MappingSchema ms = MappingSchema.fromClass(soapClazz);
			RootElementSchema res = ms.getRootElementSchema();
			
			String xmlName = res.getXmlName();
			
			// simple validation only for root element
			if (!xmlName.equalsIgnoreCase(rootElement.getLocalName())) {
				throw new ReaderException("Root element name mismatch, " + rootElement.getLocalName() + " != " + xmlName);
			}
			
			ReadContext readContext = CONTEXT.get();
			readContext.innerClass = innerClazz;
			
			Object obj = this.buildObjectFromType(soapClazz);
			
			this.read(obj, rootElement);
			
			return (T)obj;
		} catch (MappingException me) {
			throw me;
		} catch (Exception e) {
			throw new ReaderException("Error to read/descrialize object", e);
		}
	}
	
	
	public <T> T read(Class<? extends T> soapClazz, Class<?> innerClazz, String source)
			throws ReaderException, MappingException {
		try {
			return this.read(soapClazz, innerClazz, new ByteArrayInputStream(source.getBytes(super.format.getEncoding())));
		} catch (UnsupportedEncodingException e) {
			throw new ReaderException("Encoding is not supported", e);
		}
	}
	
	static class ReadContext {
		public Class<?> innerClass;
	}
	
	@Override
	protected void readAnyElement(Object obj, List<Element> anyElements) throws Exception {
		MappingSchema ms = MappingSchema.fromObject(obj);
		
		AnyElementSchema aes = ms.getAnyElementSchema();
		if (aes != null && anyElements != null && anyElements.size() > 0) {
			
			boolean soap11 = obj instanceof com.leansoft.nano.soap11.Body;
			boolean soap12 = obj instanceof com.leansoft.nano.soap12.Body;
			
			if (soap11 || soap12) {
				ReadContext context = CONTEXT.get();
				Class<?> bindClass = context.innerClass;
				
				boolean success = this.readAnyElement(obj, anyElements, bindClass);
				if (!success) {
					Class<?> soapFault = soap11? com.leansoft.nano.soap11.Fault.class : com.leansoft.nano.soap12.Fault.class;
					this.readAnyElement(obj, anyElements, soapFault);
				}
				
			} else {
				Field field = aes.getField();
				field.set(obj, anyElements);
			}
		}
	}
	
	private boolean readAnyElement(Object obj, List<Element> anyElements, Class<?> bindClass) throws Exception {
		boolean result = false;
		
		MappingSchema ms = MappingSchema.fromClass(bindClass);
		RootElementSchema res = ms.getRootElementSchema();
		
		String xmlName = res.getXmlName();
		
		List<Element> matchElements = new ArrayList<Element>();
		for(Element element : anyElements) {
			if (xmlName.equals(element.getLocalName())) {
				matchElements.add(element);
			}
		}
		
		if (matchElements.size() > 0) {
			ms = MappingSchema.fromObject(obj);
			
			AnyElementSchema aes = ms.getAnyElementSchema();
			Field field = aes.getField();
			List<Object> list = new ArrayList<Object>();
			field.set(obj, list);
			
			for(Element childElement : matchElements) {
				Object newObj = this.buildObjectFromType(bindClass);
				super.read(newObj, childElement);
				list.add(newObj);
			}
			
			result = true;
		}
		
		return result;
		
	}


}
