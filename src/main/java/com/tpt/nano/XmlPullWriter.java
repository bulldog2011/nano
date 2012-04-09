package com.tpt.nano;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.RootElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.StringUtil;

/**
 * IWriter implementation using kxml pull parser.
 * 
 * @author bulldog
 *
 */
public class XmlPullWriter implements IWriter {
	
	private static final String IDENT_PROPERTY = "http://xmlpull.org/v1/doc/features.html#indent-output";

	private Format format;

	private XmlPullParserFactory factory;

	public XmlPullWriter() {
		this(new Format());
	}

	public XmlPullWriter(Format format) {
		this.format = format;

		try {
			factory = XmlPullParserFactory.newInstance(System
					.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
		} catch (XmlPullParserException e) {
			throw new RuntimeException(
					"Failed to create XmlPullParserFactory!", e);
		}
	}

	public void write(Object source, Writer out) throws WriterException, MappingException {
		try {
			// entry validation
			validate(source, out);
	
			XmlSerializer serializer = factory.newSerializer();
			if (format.isIndent()) {
				serializer.setFeature(IDENT_PROPERTY, true);
			}
			serializer.setOutput(out);
			serializer.startDocument(format.getEncoding(), null);
			
			MappingSchema ms = MappingSchema.fromObject(source);
			RootElementSchema res = ms.getRootElementSchema();
			String namespace = res.getNamespace();
			String xmlName = res.getXmlName();
			
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
	
	private void validate(Object source, Writer out) {
		if (source == null) {
			throw new IllegalArgumentException("Can not write null instance!");
		}
		
		if (out == null) {
			throw new IllegalArgumentException("Writer is null!");
		}
		
		if (Transformer.isPrimitive(source.getClass())) {
			throw new IllegalArgumentException("Can not write primitive or enum type object, " +
					"only Nano bindable complex type object can be accepted!");
		}
	}

	public void write(Object source, OutputStream os) throws WriterException, MappingException {
		try {
			this.write(source, new OutputStreamWriter(os, format.getEncoding()));
		} catch (UnsupportedEncodingException e) {
			throw new WriterException("Error to write/serialize object", e);
		}
	}

	public String write(Object source) throws WriterException, MappingException {
		StringWriter out = new StringWriter();
		this.write(source, out);
		return out.toString();
	}
	
	private void writeObject(XmlSerializer serializer, Object source, String namespace) throws Exception {
		MappingSchema ms = MappingSchema.fromObject(source);
		
		// write xml attributes first
		writeAttributes(serializer, source, ms);
		
		// write xml value if any
		writeValue(serializer, source, ms);
		
		// write xml elements last
		writeElements(serializer, source, ms, namespace);

	}
	
	private void writeAttributes(XmlSerializer serializer, Object source, MappingSchema ms) throws Exception {
		Map<String, AttributeSchema> field2AttributeSchemaMapping = ms.getField2AttributeSchemaMapping();
		for(String fieldName : field2AttributeSchemaMapping.keySet()) {
			AttributeSchema as = field2AttributeSchemaMapping.get(fieldName);
			Field field = as.getField();
			Object value = field.get(source);
			if (value != null) {
				String attValue = Transformer.write(value, field.getType());
				if (!StringUtil.isEmpty(attValue)) {
					serializer.attribute(null, as.getXmlName(), attValue);
				}
			}
		}
	}
	
	private void writeValue(XmlSerializer serializer, Object source, MappingSchema ms) throws Exception {
		ValueSchema vs = ms.getValueSchema();
		if (vs == null) return; // no ValueSchema, do nothing
		
		Field field = vs.getField();
		Object value = field.get(source);
		if (value != null) {
			String text = Transformer.write(value, field.getType());
			if (!StringUtil.isEmpty(text)) {
				if(vs.isData()) {
					serializer.cdsect(text);
				} else {
					serializer.text(text);
				}
			}
		}
	}
	
	private void writeElements(XmlSerializer serializer, Object source, MappingSchema ms, String namespace) throws Exception {
		Map<String, Object> field2SchemaMapping = ms.getField2SchemaMapping();
		for (String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if (schemaObj instanceof ElementSchema) {
				ElementSchema es = (ElementSchema)schemaObj;
				Field field = es.getField();
				Object value = field.get(source);
				if (value != null) {
					if (es.isList()) {
						this.writeElementList(serializer, value, es, namespace);
					} else {
						this.writeElement(serializer, value, es, namespace);
					}
				}
			}
		}
	}
	
	private void writeElementList(XmlSerializer serializer, Object source, ElementSchema es, String namespace) throws Exception {
		for(Object value : (List<?>)source) {
			this.writeElement(serializer, value, es, namespace);
		}
	}
	
	private void writeElement(XmlSerializer serializer, Object source, ElementSchema es, String namespace) throws Exception {
		Class<?> type = null;
		if (es.isList()) {
			type = es.getParameterizedType();
		} else {
			type = es.getField().getType();
		}
		
		if (source == null) return; // do nothing
		
		String xmlName = es.getXmlName();
		
		// primitives
		if(Transformer.isPrimitive(type)) {
			String value = Transformer.write(source, type);
			if(StringUtil.isEmpty(value)) return;
			
			serializer.startTag(namespace, xmlName);
			if(es.isData()) {
				serializer.cdsect(value);
			} else {
				serializer.text(value);
			}
			serializer.endTag(namespace, xmlName);
			
			return;
		}
		
		// object
		serializer.startTag(namespace, xmlName);
		this.writeObject(serializer, source, namespace);
		serializer.endTag(namespace, xmlName);
	}

}
