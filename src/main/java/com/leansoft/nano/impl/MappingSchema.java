package com.leansoft.nano.impl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.leansoft.nano.annotation.AnyElement;
import com.leansoft.nano.annotation.Attribute;
import com.leansoft.nano.annotation.Default;
import com.leansoft.nano.annotation.Element;
import com.leansoft.nano.annotation.RootElement;
import com.leansoft.nano.annotation.Value;
import com.leansoft.nano.annotation.schema.AnyElementSchema;
import com.leansoft.nano.annotation.schema.AttributeSchema;
import com.leansoft.nano.annotation.schema.ElementSchema;
import com.leansoft.nano.annotation.schema.RootElementSchema;
import com.leansoft.nano.annotation.schema.ValueSchema;
import com.leansoft.nano.exception.MappingException;
import com.leansoft.nano.transform.Transformer;
import com.leansoft.nano.util.LRUCache;
import com.leansoft.nano.util.StringUtil;
import com.leansoft.nano.util.TypeReflector;

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
	private ValueSchema valueSchema;
	private Map<String, AttributeSchema> xml2AttributeSchemaMapping;
	private AnyElementSchema anyElementSchema;
	
	private Class<?> type;
	private boolean isDefault;
	
	private static final int CACHE_SIZE = 100;
	// use LRU cache to limit memory consumption.
	private static Map<Class<?>, MappingSchema> schemaCache = Collections.synchronizedMap(new LRUCache<Class<?>, MappingSchema>(CACHE_SIZE));
	
	private MappingSchema(Class<?> type) throws MappingException {
		this.type = type;
		
		isDefault = type.isAnnotationPresent(Default.class);
		
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
		rootElementSchema = new RootElementSchema();
		if (type.isAnnotationPresent(RootElement.class)) {
			RootElement xre = type.getAnnotation(RootElement.class);
			if (StringUtil.isEmpty(xre.name())) {
				rootElementSchema.setXmlName(StringUtil.lowercaseFirstLetter(type.getSimpleName()));
			} else {
				rootElementSchema.setXmlName(xre.name());
			}
			String namespace = StringUtil.isEmpty(xre.namespace())?null:xre.namespace();
			rootElementSchema.setNamespace(namespace);
		} else { // if no RootElement, use class name instead
			rootElementSchema.setXmlName(StringUtil.lowercaseFirstLetter(type.getSimpleName()));
			rootElementSchema.setNamespace(null);
		}
	}
	
	private void buildField2SchemaMapping() throws MappingException {
		field2SchemaMapping = this.scanFieldSchema(type);
		
		Class<?> superType = type.getSuperclass();
		// scan super class fields
		while(superType != Object.class && superType != null) {
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
				xml2AttributeSchemaMapping.put(as.getXmlName(), as);
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
	
	private Map<String, Object> scanFieldSchema(Class<?> type) throws MappingException {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		Field[] fields = type.getDeclaredFields();
		
		// used for validation
		int valueSchemaCount = 0;
		int anyElementSchemaCount = 0;
		int elementSchemaCount = 0;
		
		for(Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			if (field.isAnnotationPresent(Element.class)) {
				
				elementSchemaCount++;
				
				Element xmlElement = field.getAnnotation(Element.class);
				ElementSchema elementSchema = new ElementSchema();
				
				if(StringUtil.isEmpty(xmlElement.name())) {
					elementSchema.setXmlName(field.getName());
				} else {
					elementSchema.setXmlName(xmlElement.name());
				}
				
				// List validation
				handleList(field, elementSchema);
				
				elementSchema.setData(xmlElement.data());
				elementSchema.setField(field);
				
				fieldsMap.put(field.getName(), elementSchema);
				
			} else if (field.isAnnotationPresent(Attribute.class)) {
				// validation
				if (!Transformer.isTransformable(field.getType())) {
					throw new MappingException("Attribute annotation can't annotate complex type field, " +
							"only primivte type or frequently used java type or enum type field is allowed, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				Attribute xmlAttribute = field.getAnnotation(Attribute.class);
				AttributeSchema attributeSchema = new AttributeSchema();
				// if attribute name was not provided, use field name instead
				if (StringUtil.isEmpty(xmlAttribute.name())) {
					attributeSchema.setXmlName(field.getName());
				} else {
					attributeSchema.setXmlName(xmlAttribute.name());
				}
				attributeSchema.setField(field);
				
				fieldsMap.put(field.getName(), attributeSchema);
			}  else if (field.isAnnotationPresent(Value.class)) {
				valueSchemaCount++;
				
				// validation
				if (!Transformer.isTransformable(field.getType())) {
					throw new MappingException("Value annotation can't annotate complex type field, " +
							"only primivte type or frequently used java type or enum type field is allowed, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				Value xmlValue = field.getAnnotation(Value.class);
				
				valueSchema = new ValueSchema();
				valueSchema.setData(xmlValue.data());
				valueSchema.setField(field);
				
			} else if (field.isAnnotationPresent(AnyElement.class)) {
				anyElementSchemaCount++;
				
				if (!TypeReflector.collectionAssignable(field.getType())) {
					throw new MappingException("Current nano framework only supports java.util.List<T> as container of any type, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				Class<?> fieldType = field.getType();
				if (!TypeReflector.isList(fieldType)) {
					throw new MappingException("Current nano framework only supports java.util.List<T> as collection type, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				anyElementSchema = new AnyElementSchema();
				anyElementSchema.setField(field);
				
		    } else if (isDefault) { // default to Element
				elementSchemaCount++;
				
				ElementSchema elementSchema = new ElementSchema();
				
				elementSchema.setXmlName(field.getName());
				
				// List validation
				handleList(field, elementSchema);
				
				elementSchema.setField(field);
				
				fieldsMap.put(field.getName(), elementSchema);
			}
		}
		
		// more validation
		if (valueSchemaCount > 1) {
			throw new MappingException("Value annotation can't annotate more than one fields in same class," + 
					" type = " + type.getName());
		}
		
		if (anyElementSchemaCount > 1) {
			throw new MappingException("AnyElement annotation can't annotate more than one fields in same class," + 
					" type = " + type.getName());
		}
		
		if (valueSchemaCount == 1 && elementSchemaCount >= 1) {
			throw new MappingException("Value and Element annotations can't coexist in same class," + 
					" type = " + type.getName());
		}
		
		return fieldsMap;
		
	}
	
	private void handleList(Field field, ElementSchema elementSchema) throws MappingException {
		if (TypeReflector.collectionAssignable(field.getType())) {
			Class<?> type = field.getType();
			if (!TypeReflector.isList(type)) {
				throw new MappingException("Current nano framework only supports java.util.List<T> as collection type, " +
						"field = " + field.getName() + ", type = " + type.getName());
			} else {
				elementSchema.setList(true);
				Class<?> paramizedType = TypeReflector.getParameterizedType(field);
				if (paramizedType == null) {
					throw new MappingException("Can't get parameterized type of a List field, " +
							"Nano framework only supports collection field of List<T> type, and T must be a Nano bindable type, " +
							"field = " + field.getName() + ", type = " + type.getName());
				} else {
					elementSchema.setParameterizedType(paramizedType);
				}
			}
		}
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
	public static MappingSchema fromObject(Object object) throws MappingException {
		return fromClass(object.getClass());
	}
	
	/**
	 * Factory method.
	 * 
	 * @param type a Class type to get mapping schema from.
	 * @return a MappingSchema instance.
	 */
	public static MappingSchema fromClass (Class<?> type) throws MappingException {
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
	
	public AnyElementSchema getAnyElementSchema() {
		return anyElementSchema;
	}
	
	public Map<String, AttributeSchema> getXml2AttributeSchemaMapping() {
		return xml2AttributeSchemaMapping;
	}
	
}
