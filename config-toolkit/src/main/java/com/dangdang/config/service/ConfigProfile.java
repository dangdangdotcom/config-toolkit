package com.dangdang.config.service;

import java.util.Set;

/**
 * 配置组参数
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class ConfigProfile {

	/**
	 * 节点下属性的加载模式
	 * 
	 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
	 *
	 */
	public static enum KeyLoadingMode {
		/**
		 * 加载所有属性
		 */
		ALL,
		/**
		 * 包含某些属性
		 */
		INCLUDE,
		/**
		 * 排除某些属性
		 */
		EXCLUDE;
	}

	/**
	 * 项目配置版本
	 */
	protected final String version;
	
	private KeyLoadingMode keyLoadingMode = KeyLoadingMode.ALL;

	/**
	 * 需要包含或排除的key,由{@code KeyLoadingMode}决定
	 */
	private Set<String> keysSpecified;

	public ConfigProfile(String version) {
		super();
		this.version = version;
	}

	public final String getVersion() {
		return version;
	}

	public ConfigProfile.KeyLoadingMode getKeyLoadingMode() {
		return keyLoadingMode;
	}

	public void setKeyLoadingMode(ConfigProfile.KeyLoadingMode keyLoadingMode) {
		this.keyLoadingMode = keyLoadingMode;
	}

	public Set<String> getKeysSpecified() {
		return keysSpecified;
	}

	public void setKeysSpecified(Set<String> keysSpecified) {
		this.keysSpecified = keysSpecified;
	}

}
