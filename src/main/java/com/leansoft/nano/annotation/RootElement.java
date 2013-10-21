package com.leansoft.nano.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation, if presents on a POJO, 
 * indicates a root XML element
 * 
 * @author bulldog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RootElement {

	/**
	 * The name of the root XML element
	 * 
	 * @return name
	 */
    public String name() default "";
    
    /**
     * The namespace of the root XML element
     * 
     * @return namespace
     */
    public String namespace() default "";

    /**
     * The namespace prefix of the root XML element
     *
     * @return namespace prefix
     */
    public String prefix() default "";


}
