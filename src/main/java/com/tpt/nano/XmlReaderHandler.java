package com.tpt.nano;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.RootElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.StringUtil;
import com.tpt.nano.util.TypeReflector;

/**
 * SAX handler implementation for XmlSaxReader
 * 
 * @author bulldog
 * 
 */
class XmlReaderHandler extends DefaultHandler {
	
	private XmlReaderHelper helper;
	
	public XmlReaderHandler(XmlReaderHelper helper) {
		this.helper = helper;
	}

	private void populateAttributes(Object obj, Attributes attrs, MappingSchema ms) throws Exception {
		Map<String, AttributeSchema> xml2AttributeSchemaMapping = ms.getXml2AttributeSchemaMapping();
		for(int index = 0; index < attrs.getLength(); index++) {
			String attrName = attrs.getLocalName(index);
			
			AttributeSchema as = xml2AttributeSchemaMapping.get(attrName);
			if (as == null) continue;
			
			String attrValue = attrs.getValue(index);
			Field field = as.getField();
			Object value = Transformer.read(attrValue, field.getType());
			field.set(obj, value);
		}
	}
	
	public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
		try {
			helper.depth++;
			// clear the textBuilder
			helper.clearTextBuffer();
			
			if (helper.depth > helper.valueStack.size() + 1) {
				// unexpected xml element, just ignore
				return;
			}
			
			Object obj = helper.valueStack.peek();
			MappingSchema ms = MappingSchema.fromObject(obj);
			if(helper.isRoot()) { // first time root element mapping
				RootElementSchema res = ms.getRootElementSchema();
				String xmlName = res.getXmlName();
				String namespace = res.getNamespace();
				// validation only for root element
				String srcXmlFullname = StringUtil.isEmpty(uri)?localName:"{" + uri + "}#" + localName;
				String targetXmlFullname = StringUtil.isEmpty(namespace)?xmlName:"{" + namespace + "}#" + xmlName;
				if (!srcXmlFullname.equals(targetXmlFullname)) {
					throw new ReaderException("Root element name mismatch, " + targetXmlFullname + " != " + srcXmlFullname);
				}
				if (attrs != null && attrs.getLength() > 0) {
					this.populateAttributes(obj, attrs, ms);
				}
			} else { // sub element mapping
				Map<String, Object> xml2SchemaMapping = ms.getXml2SchemaMapping();
				Object schema = xml2SchemaMapping.get(localName);
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					
					Field field = es.getField();
					Class<?> type = field.getType();
					if (es.isList()) {
						type = es.getParameterizedType();
					}
					if (!Transformer.isPrimitive(type)) {
						MappingSchema newMs = MappingSchema.fromClass(type);
						Constructor con = null;
						try {
							con = TypeReflector.getConstructor(type);
						} catch (NoSuchMethodException nsme) {
							throw new ReaderException("No-arg constructor is missing, type = " + type.getName());
						}
						Object newObj = con.newInstance();
						if (attrs != null && attrs.getLength() > 0) {
							this.populateAttributes(newObj, attrs, newMs);
						}
						
						helper.valueStack.push(newObj);
					}
					
				}
			}
			
	    } catch (Exception ex) {
			throw new SAXException("Reading exception in startElement, " + ex.getMessage(), ex);
		}
	}
	
	public void endElement(String uri, String localName, String name) throws SAXException {
		try {
			if (helper.depth > helper.valueStack.size() + 1) {
				// unexpected xml element, just ignore
				helper.depth--;
				return;
			} else if (helper.depth == helper.valueStack.size() + 1) { // handle primitive field
				Object obj = helper.valueStack.peek();
				MappingSchema ms = MappingSchema.fromObject(obj);
				Map<String, Object> xml2SchemaMapping = ms.getXml2SchemaMapping();
				Object schema = xml2SchemaMapping.get(localName);
				if (schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					Field field = es.getField();
					String xmlData = helper.textBuilder.toString();
					if (!StringUtil.isEmpty(xmlData)) {
						if (es.isList()) {
							Class<?> paramizedType = es.getParameterizedType();
							Object value = Transformer.read(xmlData, paramizedType);
							List list = (List)field.get(obj);
							if (list == null) {
								list = new ArrayList();
								field.set(obj, list);
							}
							list.add(value);
						} else {
							Object value = Transformer.read(xmlData, field.getType());
							field.set(obj, value);
						}
					}
				}
			} else if (helper.depth == helper.valueStack.size()) { // handle object field
				Object obj = helper.valueStack.pop();
				MappingSchema ms = MappingSchema.fromObject(obj);
				
				if (helper.valueStack.size() == 0) {  // the end
					helper.valueStack.push(obj);
					helper.depth --;
					return;
				}
				
				ValueSchema vs = ms.getValueSchema();
				if (vs != null) {
					Field field = vs.getField();
					String xmlData = helper.textBuilder.toString();
					if (!StringUtil.isEmpty(xmlData)) {
						Object value = Transformer.read(xmlData, field.getType());
						field.set(obj, value);
					}
				}
				
				Object parentObj = helper.valueStack.peek();
				MappingSchema parentMs = MappingSchema.fromObject(parentObj);
				Map<String, Object> parentXml2SchemaMapping = parentMs.getXml2SchemaMapping();
				
				Object schema = parentXml2SchemaMapping.get(localName);
				if(schema != null && schema instanceof ElementSchema) {
					ElementSchema es = (ElementSchema)schema;
					Field field = es.getField();
					if (es.isList()) {
						List list = (List)field.get(parentObj);
						if (list == null) {
							list = new ArrayList();
							field.set(parentObj, list);
						}
						list.add(obj);
					} else {
						field.set(parentObj, obj);
					}
				}
				
			}
			
			helper.depth--;
    	} catch (Exception ex) {
    		throw new SAXException("Reading Exception in endElement, " + ex.getMessage(), ex);
    	}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		helper.textBuilder.append(text);
	}
}
