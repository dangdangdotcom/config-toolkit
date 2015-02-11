/**
 * 
 */
package com.dangdang.config.service.easyzk.demo;

import java.util.Map;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ExampleBeanWithConfigNode {

	private Map<String, String> propertyGroup1;

	public void someMethod() {
		System.out.println(propertyGroup1.get("string_property_key"));
	}

	public void setPropertyGroup1(Map<String, String> propertyGroup1) {
		this.propertyGroup1 = propertyGroup1;
	}

}
