package com.dangdang.config.service.easyzk.demo.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dangdang.config.service.easyzk.demo.ExampleBean;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class PlaceHodlerSupport {
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:config-toolkit-placeholder-support.xml");
			context.registerShutdownHook();
			context.start();

			ExampleBean bean = context.getBean(ExampleBean.class);
			System.out.println(bean);
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

}
