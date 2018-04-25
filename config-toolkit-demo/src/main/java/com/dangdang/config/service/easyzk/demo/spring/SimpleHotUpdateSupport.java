package com.dangdang.config.service.easyzk.demo.spring;

import com.dangdang.config.service.easyzk.demo.simple.ExampleBeanWithValueAnno;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author lmiky
 */
public class SimpleHotUpdateSupport {
	
	public static void main(String[] args) {
		try (final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("config-toolkit-simplehotupdate-support.xml")) {
			context.registerShutdownHook();
			context.start();

			ExampleBeanWithValueAnno bean = context.getBean(ExampleBeanWithValueAnno.class);

			while (true) {
				bean.someMethod();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
