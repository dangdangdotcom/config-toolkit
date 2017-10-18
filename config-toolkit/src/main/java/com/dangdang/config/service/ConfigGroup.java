package com.dangdang.config.service;

import com.dangdang.config.service.observer.ISubject;

import java.io.Closeable;
import java.util.Map;

/**
 * Configuration Group
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface ConfigGroup extends Map<String, String>, Closeable, ISubject {
	
	String get(String key);

	boolean getEnumerable();
}
