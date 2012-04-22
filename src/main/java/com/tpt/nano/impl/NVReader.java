package com.tpt.nano.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tpt.nano.Format;
import com.tpt.nano.IReader;
import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.exception.MappingException;
import com.tpt.nano.exception.ReaderException;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.FastStack;
import com.tpt.nano.util.StringUtil;
import com.tpt.nano.util.TypeReflector;

public class NVReader implements IReader {
	
	private Format format;
	
	public NVReader() {
		this(new Format());
	}
	
	public NVReader(Format format) {
		this.format = format;
	}

	public <T> T read(Class<? extends T> type, InputStream source)
			throws ReaderException, MappingException {
		try {
			return this.read(type, new InputStreamReader(source, format.getEncoding()));
		} catch (UnsupportedEncodingException e) {
			throw new ReaderException("Encoding is not supported", e);
		}
	}
	

	public <T> T read(Class<? extends T> type, Reader source)
			throws ReaderException, MappingException {
		try {
			return this.read(type, StringUtil.reader2String(source));
		} catch (IOException e) {
			throw new ReaderException("IO error!", e);
		}
	}

	public <T> T read(Class<? extends T> type, String source)
			throws ReaderException, MappingException {
		validate(type, source);
		
		try {
			List<NVP> nvpList = buildNVPList(source);
			
			Constructor con = TypeReflector.getConstructor(type);
			Object rootObj = con.newInstance();
			
			for(NVP nvp : nvpList) {
				FastStack<String> namePartStack = nvp.getNamePartStack();
				String strValue = nvp.getValue();
				if (!StringUtil.isEmpty(strValue)) {
					this.buildObject(rootObj, namePartStack, strValue);
				}
			}
			
			return (T)rootObj;
		} catch (MappingException me) {
			throw me;
//		} catch (ReaderException we) {
//			throw we;
		} catch (Exception e) {
			throw new ReaderException("Error to serialize object", e);
		}
	}
	
	private void buildObject(Object rootObject, FastStack<String> namePartStack, String strValue) throws Exception {
		
		String rootName = namePartStack.pop();
		MappingSchema ms = MappingSchema.fromObject(rootObject);
		if (!ms.getRootElementSchema().getXmlName().equals(rootName)) {
			throw new ReaderException("Root name mismatch, can not find root name : " + 
		                              ms.getRootElementSchema().getXmlName() + " in the nv string!");
		}
		
		Object leafObject = buildParentObjectChain(rootObject, namePartStack);
		
		buildLeafFields(leafObject, namePartStack, strValue);
		
	}
	
	private void buildLeafFields(Object leafObject, FastStack<String> namePartStack, String strValue) throws Exception {
		// build leaf fields
		MappingSchema ms = MappingSchema.fromObject(leafObject);
		String namePart = namePartStack.pop(); // last one
		
		if (isAttribute(namePart)) { // attribute
			String rawNamePart = removeAttributePrefix(namePart);
			Map<String, AttributeSchema> xml2AttributeSchemaMapping = ms.getXml2AttributeSchemaMapping();
			AttributeSchema as = xml2AttributeSchemaMapping.get(rawNamePart);
			if (as != null) {
				Field field = as.getField();
				Object value = Transformer.read(strValue, field.getType());
				field.set(leafObject, value);
			}
		} else {
			ValueSchema vs = ms.getValueSchema();
			if (vs != null && namePart.equals(NVWriter.VALUE_NAME)) { // value
				Field field = vs.getField();
				Object value = Transformer.read(strValue, field.getType());
				field.set(leafObject, value);
				return;
			}
			
			Map<String, Object> xml2SchemaMapping = ms.getXml2SchemaMapping();
			if (isList(namePart)) { // element list
				
				String rawNamePart = removeIndexPart(namePart);
				
				Object schema = xml2SchemaMapping.get(rawNamePart);
				
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					
					if (es.isList()) { // should be a list schema
						Field field = es.getField();
						
						List list = (List)field.get(leafObject);
						if (list == null) {
							list = new ArrayList();
							field.set(leafObject, list);
						}
						
						Class<?> type = es.getParameterizedType();
						if (Transformer.isPrimitive(type)) {
							Object value = Transformer.read(strValue, type);
							list.add(value);
						}
					}
					
				}
				
			} else { // element
				Object schema = xml2SchemaMapping.get(namePart);
				
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					if (!es.isList()) { // should not be a list schema
						Field field = es.getField();
						Class<?> type = field.getType();
						if (Transformer.isPrimitive(type)) {
							Object value = Transformer.read(strValue, field.getType());
							field.set(leafObject, value);
						}
					}
				}
			}
		}
	}
	
	private Object buildParentObjectChain(Object rootObject, FastStack<String> namePartStack) throws Exception {
		Object currentObject = rootObject;
		boolean currentObjectUpdated = true;
		
		// build parent objects
		while(namePartStack.size() > 1 && currentObjectUpdated) {
			
			String namePart = namePartStack.pop();
			currentObjectUpdated = false;
			
			MappingSchema ms = MappingSchema.fromObject(currentObject);
			Map<String, Object> xml2SchemaMapping = ms.getXml2SchemaMapping();
			
			if (isList(namePart)) { // list
				int index = getIndex(namePart);
				String rawNamePart = removeIndexPart(namePart);
				
				Object schema = xml2SchemaMapping.get(rawNamePart);
				
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					if (es.isList()) { // should be a list schema
						Field field = es.getField();
						
						List list = (List)field.get(currentObject);
						if (list == null) {
							list = new ArrayList();
							field.set(currentObject, list);
						}
						
						if (index == list.size()) { // the object is not in the list
							Class<?> type = es.getParameterizedType();
							Constructor con = TypeReflector.getConstructor(type);
							Object subObj = con.newInstance();
							list.add(subObj);
							// update current object
							currentObject = subObj;
							currentObjectUpdated = true;
						} else if (index == list.size() - 1) { // the object is in the list
							// update current object
							currentObject = list.get(index);
							currentObjectUpdated = true;
						}
					}
				}
			} else { // object element
				Object schema = xml2SchemaMapping.get(namePart);
				
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					if (!es.isList()) { // should not be a list schema
						Field field = es.getField();
						Object subObj = field.get(currentObject);
						if (subObj == null) { // the object is not set yet
							Class<?> type = field.getType();
							Constructor con = TypeReflector.getConstructor(type);
							subObj = con.newInstance();
							field.set(currentObject, subObj);
						}
						
						// update current object
						currentObject = subObj;
						currentObjectUpdated = true;
					}
				}
			}
		}
		
		if (!currentObjectUpdated) {
			return null; // illegal name part
		} else {
			return currentObject;
		}
	}
	
	private boolean isAttribute(String namePart) {
		return namePart.startsWith("@");
	}
	
	private String removeAttributePrefix(String namePart) {
		return namePart.substring(1, namePart.length());
	}
	
	private boolean isList(String namePart) {
		return namePart.indexOf("(") > 0 && namePart.endsWith(")");
	}
	
	private String removeIndexPart(String namePart) {
		int endIndex = namePart.indexOf("(");
		return namePart.substring(0, endIndex);
	}
	
	private int getIndex(String namePart) {
		int beginIndex = namePart.indexOf("(") + 1;
		int endIndex = namePart.length() - 1;
		String indexString = namePart.substring(beginIndex, endIndex);
		return Integer.parseInt(indexString);
	}
	
	private List<NVP> buildNVPList(String source) throws UnsupportedEncodingException {
		List<NVP> nvpList = new ArrayList<NVP>();
		String[] nvpStringArray = source.split("&");
		for(String nvpString : nvpStringArray) {
			NVP nvp = NVP.fromURLEncodedString(nvpString, format.getEncoding());
			if (nvp != null) {
				nvpList.add(nvp);
			}
		}
		return nvpList;
	}
	
	private <T> void validate(Class<? extends T> type, String source) throws ReaderException {
		if (type == null) {
			throw new ReaderException("Cannot read, type is null!");
		}
		if (StringUtil.isEmpty(source)) {
			throw new ReaderException("Source is empty!");			
		}
		
		if (Transformer.isPrimitive(type)) {
			throw new ReaderException("Can not read primitive or enum type object, " +
					"only Nano bindable complex type object can be accepted!");
		}
	}

}
