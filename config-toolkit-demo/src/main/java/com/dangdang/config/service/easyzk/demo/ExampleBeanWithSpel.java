package com.dangdang.config.service.easyzk.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Component
public class ExampleBeanWithSpel {

	@Value("#{propertyGroup1['string_property_key']}")
	private String stringProperty;

	@Value("#{propertyGroup1['int_property_key']}")
	private int intProperty;

	public void someMethod() {
		System.out.println(String.format("My properties: [%s] - [%s]", stringProperty, intProperty));
	}

}
