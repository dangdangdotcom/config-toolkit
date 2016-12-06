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
		// TODO Auto-generated method stub
		super.doParse(element, builder);
	}

}
