package com.dangdang.config.service.support.spring;

import org.springframework.beans.factory.annotation.InjectionMetadata;

/**
 * @author lmiky
 */
public class BeanInjectionMetaData {
    private String beanName;
    private InjectionMetadata injectionMetadata;

    public BeanInjectionMetaData() {

    }

    public BeanInjectionMetaData(String beanName, InjectionMetadata injectionMetadata) {
        this.beanName = beanName;
        this.injectionMetadata = injectionMetadata;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public InjectionMetadata getInjectionMetadata() {
        return injectionMetadata;
    }

    public void setInjectionMetadata(InjectionMetadata injectionMetadata) {
        this.injectionMetadata = injectionMetadata;
    }
}
