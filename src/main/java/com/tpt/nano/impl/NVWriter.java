package com.tpt.nano.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tpt.nano.Format;
import com.tpt.nano.IWriter;
import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.exception.MappingException;
import com.tpt.nano.exception.WriterException;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.StringUtil;

/**
 * IWriter implementation,
 * 
 * NVWriter serializes POJO into name-value query string, the serialization 
 * is guided by mapping schema defined in the POJO using Nano annotations.
 * 
 * @author bulldog
 *
 */
public class NVWriter implements IWriter {
	
	static final String VALUE_NAME = "value";
	
	private Format format;
	
	public NVWriter() {
		this(new Format());
	}
	
	public NVWriter(Format format) {
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
			
			List<NVP> nvs = new ArrayList<NVP>();
			String prefix = "";
			
			writeObject(nvs, prefix, source);
			
			if (nvs.size() == 0) {
				throw new WriterException("Error to serialize object, empty result.");
			} else {
				return join(nvs, "&");
			}
		} catch (MappingException me) {
			throw me;
		} catch (WriterException we) {
			throw we;
		} catch (IllegalArgumentException iae) {
			throw new WriterException("Entry validation failure", iae);
		} catch (Exception e) {
			throw new WriterException("Error to serialize object", e);
		}
	}
	
	// convert nv list to query string
	private String join(List<NVP> nvs, String delimiter) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for(NVP nv : nvs) {
			sb.append(nv.toURLEncodedString(format.getEncoding()));
			sb.append(delimiter);
		}
		int length = sb.length();
		if (length > 0) {
			// remove last &
			sb.setLength(length - delimiter.length());
		}
		return sb.toString();
	}
	
	private void writeObject(List<NVP> nvs, String prefix, Object source) throws Exception {
		MappingSchema ms = MappingSchema.fromObject(source);
		
		writeAttributes(nvs, prefix, source, ms);
		
		writeValue(nvs, prefix, source, ms);
		
		writeElements(nvs, prefix, source, ms);
		
	}
	
	private void writeAttributes(List<NVP> nvs, String prefix, Object source, MappingSchema ms) throws Exception {
		if (!prefix.isEmpty()) {
			prefix = prefix + ".";
		}
		Map<String, AttributeSchema> field2AttributeSchemaMapping = ms.getField2AttributeSchemaMapping();
		for(String fieldName : field2AttributeSchemaMapping.keySet()) {
			AttributeSchema as = field2AttributeSchemaMapping.get(fieldName);
			Field field = as.getField();
			Object obj = field.get(source);
			if (obj != null) {
				String name = prefix + "@" + fieldName;
				String value = Transformer.write(obj, field.getType());
				nvs.add(new NVP(name, value));
			}
		}
	}
	
	private void writeValue(List<NVP> nvs, String prefix, Object source, MappingSchema ms) throws Exception {
		ValueSchema vs = ms.getValueSchema();
		if (vs == null) return; // no ValueSchema, do nothing
		
		Field field = vs.getField();
		Object obj = field.get(source);
		if (obj != null) {
			String name = prefix + "." + VALUE_NAME;
			String value = Transformer.write(obj, field.getType());
			nvs.add(new NVP(name, value));
		}
	}
	
	private void writeElements(List<NVP> nvs, String prefix, Object source, MappingSchema ms) throws Exception {
		if (!prefix.isEmpty()) {
			prefix = prefix + ".";
		}
		Map<String, Object> field2SchemaMapping = ms.getField2SchemaMapping();
		for (String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if (schemaObj instanceof ElementSchema) {
				ElementSchema es = (ElementSchema)schemaObj;
				Field field = es.getField();
				Object value = field.get(source);
				if (value != null) {
					prefix = prefix + es.getXmlName();
					if (es.isList()) {
						this.writeElementList(nvs, prefix, value, es);
					} else {
						this.writeElement(nvs, prefix, value, es);
					}
				}
			}
		}
	}
	
	private void writeElementList(List<NVP> nvs, String prefix, Object source, ElementSchema es) throws Exception {
		List<?> list = (List<?>)source;
		if (list.size() > 0) {
			int index = 0;
			for(Object value : list) {
				if (value == null) continue;
				prefix = prefix + "(" + index + ")";
				index++;
				this.writeElement(nvs, prefix, value, es);
			}
		}
	}
	
	private void writeElement(List<NVP> nvs, String prefix, Object source, ElementSchema es) throws Exception {
		Class<?> type = null;
		if (es.isList()) {
			type = es.getParameterizedType();
		} else {
			type = es.getField().getType();
		}
		
		// primitives
		if(Transformer.isPrimitive(type)) {
			String name = prefix;
			String value = Transformer.write(source, type);
			
			nvs.add(new NVP(name, value));
			
			return;
		}
		
		// object
		this.writeObject(nvs, prefix, source);
	}

}
