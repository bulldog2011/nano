package com.tpt.nano.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This annotation, if presents on a POJO list field, 
 * indicates an XML wrapper element for the list
 * 
 * @author bulldog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlElementWrapper {
    
	/**
	 * Wrapper XML element name
	 * 
	 * @return name
	 */
	public String name() default "";
	
	/**
	 * Wrapper XML element namespace 
	 * 
	 * @return namespace
	 */
    public String namespace() default "";
}
