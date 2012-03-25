package com.tpt.nano.util;

import java.awt.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Type reflection utils.
 * 
 * @author bulldog
 *
 */
public class TypeReflector {
	
	// Cache for contructor
	private static Map<Class<?>, Constructor<?>> cache = new ConcurrentHashMap<Class<?>, Constructor<?>>();

	/**
	 * Get parameterized type of a java.util.List field,
	 * T of List<T>.
	 * 
	 * @param field a java.lang.reflect.Field object
	 * @return parameterized Class type
	 */
	public static Class<?> getParameterizedType(Field field) {
		Class<?> paramClass = null;
		
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType)genericType;
			if(type.getActualTypeArguments().length == 1 && 
			   type.getActualTypeArguments()[0] instanceof Class) {
				paramClass = (Class<?>)type.getActualTypeArguments()[0];
			}
		}
		
		return paramClass;
	}
	
	/**
	 * Reflect a constructor of a class.
	 * 
	 * @param type a Class type
	 * @return a Constructor object
	 * @throws Exception if reflection fails
	 */
	public static Constructor<?> getConstructor(Class<?> type) throws Exception {
		Constructor<?> con = cache.get(type);
		if (con != null) return con;
		con = type.getDeclaredConstructor();
		if (!con.isAccessible()) {
			con.setAccessible(true);
		}
		cache.put(type, con);
		return con;
	}
	
	/**
	 * check if a type is @see java.util.List type.
	 * 
	 * @param type a type to be check
	 * @return true or false
	 */
	public static boolean isList(Class<?> type) {
		return List.class.isAssignableFrom(type);
	}
	
	/**
	 * check if a type is @see java.util.Collection type.
	 * 
	 * @param type a type to be check
	 * @return true or false
	 */
	public static boolean isCollection(Class<?> type) {
		return Collection.class.isAssignableFrom(type);
	}
	
}
