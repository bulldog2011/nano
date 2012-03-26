package com.tpt.nano;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tpt.nano.annotation.XmlAttribute;
import com.tpt.nano.annotation.XmlElement;
import com.tpt.nano.annotation.XmlElementWrapper;
import com.tpt.nano.annotation.XmlIgnore;
import com.tpt.nano.annotation.XmlRootElement;
import com.tpt.nano.annotation.XmlValue;
import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.RootElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.LRUCache;
import com.tpt.nano.util.StringUtil;
import com.tpt.nano.util.TypeReflector;

/**
 * Factory class for OX mapping schema
 * 
 * 
 * @author bulldog
 *
 */
class MappingSchema {
	
	private RootElementSchema rootElementSchema;
	private Map<String, Object> field2SchemaMapping;
	private Map<String, Object> xml2SchemaMapping;
	
	private Map<String, AttributeSchema> field2AttributeSchemaMapping;
	//private Map<String, ElementSchema> field2ElementSchemaMapping;
	private ValueSchema valueSchema;
	private Map<String, AttributeSchema> xml2AttributeSchemaMapping;
	//private Map<String, ElementSchema> xml2ElementSchemaMapping;
	
	private Class<?> type;
	
	private static final int CACHE_SIZE = 100;
	// use LRU cache to limit memory consumption.
	private static Map<Class<?>, MappingSchema> schemaCache = Collections.synchronizedMap(new LRUCache<Class<?>, MappingSchema>(CACHE_SIZE));
	
	private MappingSchema(Class<?> type) {
		this.type = type;
		
		// step 1
		this.buildRootElementSchema();
		// step 2
		this.buildField2SchemaMapping();
		// step 3
		this.buildXml2SchemaMapping();
		// step 4
		this.buildField2AttributeSchemaMapping();
	}

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
	
	private void buildField2SchemaMapping() {
		field2SchemaMapping = this.scanFieldSchema(type);
		
		Class<?> superType = type.getSuperclass();
		// scan super class fields
		while(superType != null && superType != Object.class) {
			Map<String, Object> parentField2SchemaMapping = this.scanFieldSchema(superType);
			// redefined fields in sub-class will overwrite corresponding fields in super-class.
			parentField2SchemaMapping.putAll(field2SchemaMapping);
			field2SchemaMapping = parentField2SchemaMapping;
			superType = superType.getSuperclass();
		}
	}
	
	private void buildXml2SchemaMapping() {
		xml2SchemaMapping = new HashMap<String, Object>();
		xml2AttributeSchemaMapping = new HashMap<String, AttributeSchema>();
		
		for(String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if(schemaObj instanceof AttributeSchema) {
				AttributeSchema as = (AttributeSchema)schemaObj;
				xml2SchemaMapping.put(as.getXmlName(), as);
				// build xml2AttributeSchemaMapping at the same time.
				xml2AttributeSchemaMapping.put(fieldName, as);
			} else if (schemaObj instanceof ElementSchema) {
				ElementSchema es = (ElementSchema)schemaObj;
				xml2SchemaMapping.put(es.getXmlName(), es);
			}
		}
	}
	
	private void buildField2AttributeSchemaMapping() {
		field2AttributeSchemaMapping = new HashMap<String, AttributeSchema>();
		
		for(String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if(schemaObj instanceof AttributeSchema) {
				field2AttributeSchemaMapping.put(fieldName, (AttributeSchema)schemaObj);
			}
		}
	}
	
	private Map<String, Object> scanFieldSchema(Class<?> type) {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		Field[] fields = type.getDeclaredFields();
		
		// used for validation
		int valueSchemaCount = 0;
		int elementSchemaCount = 0;
		
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
				
				elementSchemaCount++;
				
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
					throw new MappingException("Can't get parameterized type of a List field, " +
							"Nano framework only supports List<T> type collection field, and T must be a Nano bindable type, " +
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
			} else if (field.isAnnotationPresent(XmlElement.class)) {
				
				elementSchemaCount++;
				
				XmlElement xmlElement = field.getAnnotation(XmlElement.class);
				ElementSchema elementSchema = new ElementSchema();
				
				if(StringUtil.isEmpty(xmlElement.name())) {
					elementSchema.setXmlName(field.getName());
				} else {
					elementSchema.setXmlName(xmlElement.name());
				}
				
				elementSchema.setData(xmlElement.data());
				elementSchema.setNamespace(xmlElement.namespace());
				elementSchema.setField(field);
				
				fieldsMap.put(field.getName(), elementSchema);
				
			} else if (field.isAnnotationPresent(XmlValue.class)) {
				valueSchemaCount++;
				
				valueSchema = new ValueSchema();
				valueSchema.setField(field);
				
			} else if (field.isAnnotationPresent(XmlIgnore.class)) {
				
				// ignore this field
				
			} else { // default to XmlElement
				
				elementSchemaCount++;
				
				ElementSchema elementSchema = new ElementSchema();
				
				elementSchema.setXmlName(field.getName());
				elementSchema.setField(field);
				
				fieldsMap.put(field.getName(), elementSchema);
			}
		}
		
		// more validation
		if (valueSchemaCount > 1) {
			throw new MappingException("XmlValue can't annotate more than one fields in same class," + 
					" type = " + type.getName());
		}
		
		if (valueSchemaCount == 1 && elementSchemaCount >= 1) {
			throw new MappingException("XmlValue and XmlElement(or XmlElementWrapper) annotations can't coexist in same class," + 
					" type = " + type.getName());
		}
		
		return fieldsMap;
		
	}
	
	public Class<?> getType() {
		return this.type;
	}
	
	/**
	 * Factory method.
	 * 
	 * @param object an object to get mapping schema from.
	 * @return a MappingSchema instance.
	 */
	public static MappingSchema fromObject(Object object) {
		return fromClass(object.getClass());
	}
	
	/**
	 * Factory method.
	 * 
	 * @param type a Class type to get mapping schema from.
	 * @return a MappingSchema instance.
	 */
	public static MappingSchema fromClass (Class<?> type) {
		if (schemaCache.containsKey(type)) {
			return schemaCache.get(type);
		} else {
			MappingSchema mappingSchema = new MappingSchema(type);
			schemaCache.put(type, mappingSchema);
			return mappingSchema;
		}
	}
	
	public Map<String, Object> getField2SchemaMapping() {
		return field2SchemaMapping;
	}

	public Map<String, Object> getXml2SchemaMapping() {
		return xml2SchemaMapping;
	}
	
	public RootElementSchema getRootElementSchema() {
		return rootElementSchema;
	}
	
	public Map<String, AttributeSchema> getField2AttributeSchemaMapping() {
		return field2AttributeSchemaMapping;
	}
	
	public ValueSchema getValueSchema() {
		return valueSchema;
	}
	
	public Map<String, AttributeSchema> getXml2AttributeSchemaMapping() {
		return xml2AttributeSchemaMapping;
	}
	
}
