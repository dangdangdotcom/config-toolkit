package com.dangdang.config.service.easyzk.demo.simple;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Component
@RefreshScope
@EnableAutoConfiguration // somewhere
public class ExampleBeanWithSpel {

	@Value("#{propertyGroup1['string_property_key']}")
	private String stringProperty;

	@Value("#{propertyGroup1['int_property_key']}")
	private int intProperty;

	private String computedValue;

	@PostConstruct
	private void init() {
		computedValue = stringProperty + intProperty;
	}

	public void someMethod() {
		System.out.println(String.format("My properties: [%s] - [%s] - [%s]", stringProperty, intProperty, computedValue));
	}

}
