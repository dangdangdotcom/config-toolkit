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
package com.dangdang.config.service.easyzk.demo.simple;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;

/**
 * Example bean for spring integration
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ExampleBeanWithValueAnno {

	@Value("${string_property_key}")
	private String stringProperty;

	@Value("${int_property_key}")
	private int intProperty;

	private boolean cool;

	public String getStringProperty() {
		return stringProperty;
	}

	public int getIntProperty() {
		return intProperty;
	}

	public boolean isCool() {
		return cool;
	}

	public void setCool(boolean cool) {
		this.cool = cool;
	}

	public void someMethod() {
		System.out.println(toString());
	}

	@Override
	public String toString() {
		return "ExampleBeanWithValueAnno [stringProperty=" + stringProperty + ", intProperty=" + intProperty + ", cool=" + cool + "]";
	}

	public void init() {
		System.out.println("ExampleBeanWithValueAnno init!");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("ExampleBeanWithValueAnno destroy!");
	}
}
