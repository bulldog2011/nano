package com.tpt.nano;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.HTML.Attribute;

import com.tpt.nano.annotation.XmlAttribute;
import com.tpt.nano.annotation.XmlElement;
import com.tpt.nano.annotation.XmlElementWrapper;
import com.tpt.nano.annotation.XmlRootElement;
import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.RootElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.LRUCache;
import com.tpt.nano.util.StringUtil;
import com.tpt.nano.util.TypeReflector;

class MappingSchema {
	
	private RootElementSchema rootElementSchema;
	private Map<String, Object> field2SchemaMapping;
	private Map<String, Object> xml2SchemaMapping;
	
	private Map<String, AttributeSchema> field2AttributeSchemaMapping;
	private Map<String, ElementSchema> field2ElementSchemaMapping;
	private ValueSchema valueSchema;
	private Map<String, AttributeSchema> xml2AttributeSchemaMapping;
	private Map<String, ElementSchema> xml2ElementSchemaMapping;
	
	private Class<?> type;
	
	private static final int CACHE_SIZE = 100;
	private static Map<Class<?>, MappingSchema> schemaCache = Collections.synchronizedMap(new LRUCache<Class<?>, MappingSchema>(CACHE_SIZE));

	private void buildRootElementSchema() {
		if (type.isAnnotationPresent(XmlRootElement.class)) {
			XmlRootElement xre = type.getAnnotation(XmlRootElement.class);
			rootElementSchema = new RootElementSchema();
			if (StringUtil.isEmpty(xre.name())) {
				rootElementSchema.setXmlName(StringUtil.lowercaseFirstLetter(type.getSimpleName()));
			} else {
				rootElementSchema.setXmlName(xre.name());
			}
			rootElementSchema.setNamespace(xre.namespace());
		}
	}
	
	private Map<String, Object> scanFieldSchema(Class<?> type) {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		Field[] fields = type.getDeclaredFields();
		for(Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			if (field.isAnnotationPresent(XmlAttribute.class)) {
				// validation
				if (!Transformer.isTransformable(field.getType())) {
					throw new MappingException("XmlAttribute can't annotate complex type field, " +
							"only primivte type or frequently used java type or enum type field is allowed, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
				AttributeSchema attributeSchema = new AttributeSchema();
				// if attribute name was not provided, use field name instead
				if (StringUtil.isEmpty(xmlAttribute.name())) {
					attributeSchema.setXmlName(field.getName());
				} else {
					attributeSchema.setXmlName(xmlAttribute.name());
				}
				attributeSchema.setNamespace(xmlAttribute.namespace());
				attributeSchema.setField(field);
				
				fieldsMap.put(field.getName(), attributeSchema);
			} else if (field.isAnnotationPresent(XmlElementWrapper.class)) {
				// validation
				if (!TypeReflector.isList(field.getType())) {
					if (TypeReflector.isCollection(field.getType())) {
						throw new MappingException("Nano framework only supports java.util.List as collection type, " +
								"field = " + field.getName() + ", type = " + type.getName());
					} else {
						throw new MappingException("XmlElementWrapper can't annotate non java.util.List type field, " +
								"field = " + field.getName() + ", type = " + type.getName());
					}
				}
				
				XmlElementWrapper xmlElementWrapper = field.getAnnotation(XmlElementWrapper.class);
				ElementSchema elementSchema = new ElementSchema();
				
				elementSchema.setList(true);
				Class<?> paramizedType = TypeReflector.getParameterizedType(field);
				if (paramizedType == null) {
					throw new MappingException("Can't get paramized type of a List field, " +
							"Nano framework only supports List<T> type List field, and T must be an instantiatable Class, " +
							"field = " + field.getName() + ", type = " + type.getName());
				} else {
					elementSchema.setParameterizedType(paramizedType);
				}
				
				elementSchema.setWrapperElement(true);
				elementSchema.setField(field);
				
				// wrapper element name must be provided
				if (StringUtil.isEmpty(xmlElementWrapper.name())) {
					throw new MappingException("Missing wrapper element name in XmlElementWrapper annotation, " +
							"field = " + field.getName() + ", type = " + type.getName());
				} else {
					elementSchema.setXmlName(xmlElementWrapper.name());
				}
				// if wrapper element entry name was not provided, use field name instead.
				if (StringUtil.isEmpty(xmlElementWrapper.entryName())) {
					elementSchema.setEntryXmlName(field.getName());
				} else {
					elementSchema.setEntryXmlName(xmlElementWrapper.entryName());
				}
				
				elementSchema.setNamespace(xmlElementWrapper.namespace());
				
				fieldsMap.put(field.getName(), elementSchema);
			}
		}
		
		return fieldsMap;
		
	}
	
}
