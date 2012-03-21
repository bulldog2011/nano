package com.tpt.nano.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This annotation maps a POJO field to an XML attribute
 * 
 * @author bulldog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlAttribute {

	/**
	 * The name of the XML attribute
	 * 
	 * @return name
	 */
	public String name() default "";
	
	/**
	 * The namespace of the XML element
	 * 
	 * @return namespace
	 */
	public String namespace() default "";
	
}
