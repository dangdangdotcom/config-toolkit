package com.dangdang.config.service.support.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConfigToolkitNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("profile", new ZookeeperConfigProfileConfigBean());
		registerBeanDefinitionParser("group", new ZookeeperConfigGroupConfigBean());
		registerBeanDefinitionParser("placeholder", new PropertySourcesPlaceholderConfigurerConfigBean());
	}

}
