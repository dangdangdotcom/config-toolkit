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

	/**
	 * 获取配置
	 * @param key
	 * @return
	 */
	String get(String key);

	/**
	 * 兼容spring,是否通过EnumerablePropertySource加载配置组
	 * @return
	 */
	boolean isEnumerable();
}
