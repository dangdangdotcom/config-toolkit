/**
 * 
 */
package com.dangdang.config.service.easyzk.demo;

import com.dangdang.config.service.easyzk.ConfigNode;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ExampleBeanWithConfigNode {

	private ConfigNode propertyGroup1;

	public void someMethod() {
		System.out.println(propertyGroup1.get("string_property_key"));
	}

	public void setPropertyGroup1(ConfigNode propertyGroup1) {
		this.propertyGroup1 = propertyGroup1;
	}

}
