package com.dangdang.config.service.support.spring;

/**
 * @author lmiky
 */
public class BeanInjectionFieldData {
    private String beanName;
    private String beanField;

    public BeanInjectionFieldData() {

    }

    public BeanInjectionFieldData(String beanName, String beanField) {
        this.beanName = beanName;
        this.beanField = beanField;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanField() {
        return beanField;
    }

    public void setBeanField(String beanField) {
        this.beanField = beanField;
    }
}
