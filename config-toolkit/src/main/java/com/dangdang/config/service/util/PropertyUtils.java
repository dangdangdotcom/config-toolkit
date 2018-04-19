package com.dangdang.config.service.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author lmiky
 */
public class PropertyUtils {

	/**
	 * @param object
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object object, Field field, String value) {
		ReflectionUtils.makeAccessible(field);
		Class<?> typeClass = field.getType();
		if(String.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, value);
		} else if(short.class.isAssignableFrom(typeClass) || Short.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Short.valueOf(value));
		} else if(int.class.isAssignableFrom(typeClass) || Integer.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Integer.valueOf(value));
		} else if(long.class.isAssignableFrom(typeClass) || Long.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Long.valueOf(value));
		} else if(float.class.isAssignableFrom(typeClass) || Float.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Float.valueOf(value));
		} else if(double.class.isAssignableFrom(typeClass) || Double.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Double.valueOf(value));
		} else if(byte.class.isAssignableFrom(typeClass) || Byte.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Byte.valueOf(value));
		} else if(boolean.class.isAssignableFrom(typeClass) || Boolean.class.isAssignableFrom(typeClass)) {
			ReflectionUtils.setField(field, object, Boolean.valueOf(value));
		}
	}
}
