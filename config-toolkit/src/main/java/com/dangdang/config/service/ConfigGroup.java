package com.dangdang.config.service;

/**
 * Configuration Group
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface ConfigGroup {
	
	String get(String key);
	
	void destroy();

}
