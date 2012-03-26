package com.tpt.nano.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This annotation, if presents on a POJO list field, 
 * indicates an XML wrapper element for the list.
 * 
 * This annotation can only annotate java.util.List<T> type field, and T must be
 * a Nano bindable type.
 * 
 * @author bulldog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlElementWrapper {
    
	/**
	 * Wrapper XML element name, must be provided
	 * 
	 * @return name
	 */
	public String name() default "";
	
	
	/**
	 * Entry XML Element name, if not provided, field name will be used instead.
	 * 
	 * @return entry name
	 */
	public String entryName() default "";
	
	/**
	 * Wrapper XML element namespace 
	 * 
	 * @return namespace
	 */
    public String namespace() default "";
}
