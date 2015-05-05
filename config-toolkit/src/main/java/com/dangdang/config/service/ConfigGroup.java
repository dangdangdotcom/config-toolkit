package com.dangdang.config.service;

import java.io.Closeable;
import java.util.Map;

/**
 * Configuration Group
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface ConfigGroup extends Map<String, String>, Closeable {
	
	String get(String key);
	
}
