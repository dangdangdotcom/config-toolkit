package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author lmiky
 */
public class ValueExpressionPlaceHolderBeanFactoryPostProcessor extends ValueInjectionPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValueExpressionPlaceHolderBeanFactoryPostProcessor.class);
    private InjectionDataHandler injectionDataHandler;
    private Set<String> excludeScanPackages;
    private Set<String> scanPackages;
    private int order;

    public ValueExpressionPlaceHolderBeanFactoryPostProcessor() {
        super();
        setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if(beanDefinition == null) {
                continue;
            }
            String beanClassName = beanDefinition.getBeanClassName();
            if(!isScan(beanClassName)) {
                continue;
            }
            MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
            if(propertyValues == null) {
                continue;
            }
            if(CollectionUtils.isEmpty(propertyValues.getPropertyValueList())) {
                continue;
            }
            for(PropertyValue propertyValue : propertyValues.getPropertyValueList()) {
                String fieldName = propertyValue.getName();
                Object value = propertyValue.getValue();
                if(value instanceof TypedStringValue) {
                    TypedStringValue typedStringValue = (TypedStringValue) value;
                    String stringValue = typedStringValue.getValue();
                    if (!StringUtils.isEmpty(stringValue)) {
                        String injectionKey = SpringUtils.extractPlaceHolderKey(stringValue);
                        if(stringValue.equals(injectionKey)) {
                            continue;
                        }
                        injectionDataHandler.addInjection(injectionKey, new BeanInjectionFieldData(beanName, fieldName));
                        LOGGER.info("add bean[{}] expression field[{}] key[{}] to InjectionHandler!", beanName, fieldName, stringValue);
                    }
                }
            }
        }
    }

    public InjectionDataHandler getInjectionDataHandler() {
        return injectionDataHandler;
    }

    public void setInjectionDataHandler(InjectionDataHandler injectionDataHandler) {
        this.injectionDataHandler = injectionDataHandler;
    }

    @Override
    public Set<String> getExcludeScanPackages() {
        return excludeScanPackages;
    }

    public void setExcludeScanPackages(Set<String> excludeScanPackages) {
        this.excludeScanPackages = excludeScanPackages;
    }

    @Override
    public Set<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(Set<String> scanPackages) {
        this.scanPackages = scanPackages;
    }


    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
