package com.dangdang.config.service;

import java.io.Closeable;

/**
 * Configuration Group
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface ConfigGroup extends Closeable {
	
	String get(String key);
	
}
