/**
 * 
 */
package com.dangdang.config.service.easyzk.demo;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Component
public class ExampleBeanWithConfigNode {

	@Resource
	private Map<String, String> propertyGroup1;

	public void someMethod() {
		System.out.println(propertyGroup1.get("string_property_key"));
	}

}
