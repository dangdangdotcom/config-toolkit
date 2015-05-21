package com.dangdang.config.service.easyzk.demo.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dangdang.config.service.easyzk.demo.ExampleBeanWithSpel;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class SpelSupportTest {
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:config-toolkit-java-config.xml");
			context.registerShutdownHook();
			context.start();

			ExampleBeanWithSpel bean = context.getBean(ExampleBeanWithSpel.class);
			bean.someMethod();
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

}
