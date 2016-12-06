package com.dangdang.config.service.support.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

public class ZookeeperConfigProfileConfigBean extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ZookeeperConfigProfile.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String connectStr = element.getAttribute("connect-str");
		builder.addConstructorArgValue(connectStr);
		String rootNode = element.getAttribute("root-node");
		builder.addConstructorArgValue(rootNode);
		String version = element.getAttribute("version");
		builder.addConstructorArgValue(version);
	}

}
