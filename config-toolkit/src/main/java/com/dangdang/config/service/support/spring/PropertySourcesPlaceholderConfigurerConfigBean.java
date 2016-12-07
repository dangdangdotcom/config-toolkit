package com.dangdang.config.service.support.spring;

import java.util.List;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.w3c.dom.Element;

public class PropertySourcesPlaceholderConfigurerConfigBean extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return PropertySourcesPlaceholderConfigurer.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		builder.addPropertyValue("order", Integer.parseInt(element.getAttribute("order")));
		builder.addPropertyValue("ignoreUnresolvablePlaceholders", Boolean.valueOf(element.getAttribute("ignore-unresolvable-placeholders")));

		List<Object> list = parserContext.getDelegate().parseListElement(element, builder.getRawBeanDefinition());

		// Register property sources
		BeanDefinitionBuilder configGroupSourceFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConfigGroupSourceFactory.class);
		configGroupSourceFactoryBuilder.setFactoryMethod("create");
		configGroupSourceFactoryBuilder.addConstructorArgValue(list);
		String generatedSourceFactoryName = parserContext.getReaderContext().generateBeanName(configGroupSourceFactoryBuilder.getRawBeanDefinition());
		parserContext
				.registerBeanComponent(new BeanComponentDefinition(configGroupSourceFactoryBuilder.getBeanDefinition(), generatedSourceFactoryName));

		builder.addPropertyValue("propertySources", new RuntimeBeanReference(generatedSourceFactoryName));
	}

}
