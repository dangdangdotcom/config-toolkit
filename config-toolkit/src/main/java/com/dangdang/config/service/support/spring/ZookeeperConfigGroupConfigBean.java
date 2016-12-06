package com.dangdang.config.service.support.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;

public class ZookeeperConfigGroupConfigBean extends AbstractSingleBeanDefinitionParser {
	@Override
	protected Class<?> getBeanClass(Element element) {
		return ZookeeperConfigGroup.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String configProfileRef = element.getAttribute("config-profile-ref");
		builder.addConstructorArgReference(configProfileRef);
		String node = element.getAttribute("node");
		builder.addConstructorArgValue(node);
	}

}
