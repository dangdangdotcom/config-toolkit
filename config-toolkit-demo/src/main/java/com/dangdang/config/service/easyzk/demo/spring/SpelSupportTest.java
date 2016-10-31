package com.dangdang.config.service.easyzk.demo.spring;

import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.easyzk.demo.simple.ExampleBeanWithSpel;
import com.dangdang.config.service.observer.IObserver;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class SpelSupportTest {

	public static void main(String[] args) {
		try (final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:config-toolkit-simple.xml")) {
			context.registerShutdownHook();
			context.start();

			ExampleBeanWithSpel bean = context.getBean(ExampleBeanWithSpel.class);
			GeneralConfigGroup cg1 = (GeneralConfigGroup) context.getBean("propertyGroup1");
			
			cg1.register(new IObserver() {

				@Override
				public void notified(String data, String value) {
					context.getBean(RefreshScope.class).refresh("exampleBeanWithSpel");
				}
			});

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
