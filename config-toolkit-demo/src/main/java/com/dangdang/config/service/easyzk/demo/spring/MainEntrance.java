/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.service.easyzk.demo.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dangdang.config.service.easyzk.demo.ExampleBeanWithConfigNode;

/**
 * Load spring, and validation the property
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class MainEntrance {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:config-toolkit-simple.xml");
			context.registerShutdownHook();
			context.start();

			ExampleBeanWithConfigNode bean = context.getBean(ExampleBeanWithConfigNode.class);
			while (true) {
				bean.someMethod();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//
				}
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
}
