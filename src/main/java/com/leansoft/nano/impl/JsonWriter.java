package com.leansoft.nano.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.json.JSONStringer;

import com.leansoft.nano.Format;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.annotation.schema.AttributeSchema;
import com.leansoft.nano.annotation.schema.ElementSchema;
import com.leansoft.nano.annotation.schema.RootElementSchema;
import com.leansoft.nano.annotation.schema.ValueSchema;
import com.leansoft.nano.exception.MappingException;
import com.leansoft.nano.exception.WriterException;
import com.leansoft.nano.transform.Transformer;
import com.leansoft.nano.util.StringUtil;


/**
 * IWriter implementation using org.json library,
 * 
 * JsonWriter serializes POJO into JSON string, the serialization 
 * is guided by mapping schema defined in the POJO using Nano annotations.
 * 
 * @author bulldog
 *
 */
public class JsonWriter implements IWriter {

	static final String VALUE_KEY = "__value__";
	
	private Format format;
	
	public JsonWriter() {
		this(new Format());
	}
	
	public JsonWriter(Format format) {
		this.format = format;
	}
	
	public void write(Object source, Writer out) throws WriterException,
			MappingException {
		if (out == null) {
			throw new WriterException("Entry validation failure, Writer is null!");
		}

		String result = this.write(source);
		try {
			StringUtil.string2Writer(result, out);
		} catch (IOException e) {
			throw new WriterException("IO error", e);
		}

	}

	public void write(Object source, OutputStream os) throws WriterException,
			MappingException {

		if (os == null) {
			throw new WriterException("Entry validation failure, OutputStream is null!");
		}
		
		try {
			this.write(source, new OutputStreamWriter(os, format.getEncoding()));
		} catch (UnsupportedEncodingException e) {
			throw new WriterException("Error to serialize object", e);
		}
	}

	public String write(Object source) throws WriterException, MappingException {
		try {
			if (source == null) {
				throw new IllegalArgumentException("Can not write null instance!");
			}
			
			if (Transformer.isPrimitive(source.getClass())) {
				throw new IllegalArgumentException("Can not write primitive or enum type object, " +
						"only Nano bindable complex type object can be accepted!");
			}
			
			JSONStringer stringer = new JSONStringer();
			
			stringer.object();
			
			MappingSchema ms = MappingSchema.fromObject(source);
			RootElementSchema res = ms.getRootElementSchema();
			
			stringer.key(res.getXmlName());
			this.writeObject(stringer, source);
			
			stringer.endObject();
			// output
			return stringer.toString();
		
		} catch (MappingException me) {
			throw me;
		} catch (IllegalArgumentException iae) {
			throw new WriterException("Entry validation failure", iae);
		} catch (Exception e) {
			throw new WriterException("Error to serialize object", e);
		}
	}
	
//	private void string2Writer(String source, Writer out) throws IOException {
//		
//		char[] buffer = source.toCharArray();
//		for(int i = 0; i < buffer.length; i++) {
//			out.append(buffer[i]);
//		}
//		out.flush();
//	}
	
	private void writeObject(JSONStringer stringer, Object source) throws Exception {
		MappingSchema ms = MappingSchema.fromObject(source);
		
		stringer.object();
		
		// write attributes first
		writeAttributes(stringer, source, ms);
		
		// write value if any
		writeValue(stringer, source, ms);
		
		// write elements last
		writeElements(stringer, source, ms);
		
		stringer.endObject();

	}
	
	private void writeAttributes(JSONStringer stringer, Object source, MappingSchema ms) throws Exception {
		Map<String, AttributeSchema> field2AttributeSchemaMapping = ms.getField2AttributeSchemaMapping();
		for(String fieldName : field2AttributeSchemaMapping.keySet()) {
			AttributeSchema as = field2AttributeSchemaMapping.get(fieldName);
			Field field = as.getField();
			Object value = field.get(source);
			if (value != null) {
				stringer.key("@" + as.getXmlName());
				stringer.value(getJSONValue(value, field.getType()));
			}
		}
	}
	
	private void writeValue(JSONStringer stringer, Object source, MappingSchema ms) throws Exception {
		ValueSchema vs = ms.getValueSchema();
		if (vs == null) return; // no ValueSchema, do nothing
		
		Field field = vs.getField();
		Object value = field.get(source);
		if (value != null) {
			stringer.key(VALUE_KEY);
			stringer.value(getJSONValue(value, field.getType()));
		}
	}
	
	private void writeElements(JSONStringer stringer, Object source, MappingSchema ms) throws Exception {
		Map<String, Object> field2SchemaMapping = ms.getField2SchemaMapping();
		for (String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if (schemaObj instanceof ElementSchema) {
				ElementSchema es = (ElementSchema)schemaObj;
				Field field = es.getField();
				Object value = field.get(source);
				if (value != null) {
					if (es.isList()) {
						this.writeElementList(stringer, value, es);
					} else {
						stringer.key(es.getXmlName());
						this.writeElement(stringer, value, es);
					}
				}
			}
		}
	}
	
	private void writeElementList(JSONStringer stringer, Object source, ElementSchema es) throws Exception {
		List<?> list = (List<?>)source;
		if (list.size() > 0) {
			stringer.key(es.getXmlName());
			stringer.array();
			for(Object value : list) {
				if (value == null) continue;
				this.writeElement(stringer, value, es);
			}
			stringer.endArray();
		}
	}
	
	
	private void writeElement(JSONStringer stringer, Object source, ElementSchema es) throws Exception {
		Class<?> type = null;
		if (es.isList()) {
			type = es.getParameterizedType();
		} else {
			type = es.getField().getType();
		}
		
		// primitives
		if(Transformer.isPrimitive(type)) {
			stringer.value(getJSONValue(source, type));
			
			return;
		}
		
		// object
		this.writeObject(stringer, source);
	}
	
	private Object getJSONValue(Object value, Class<?> type) throws Exception {
		if (value instanceof Number || value instanceof Boolean) {
			return value;
		}
		String stringValue = Transformer.write(value, type);
		return stringValue;
	}

}
