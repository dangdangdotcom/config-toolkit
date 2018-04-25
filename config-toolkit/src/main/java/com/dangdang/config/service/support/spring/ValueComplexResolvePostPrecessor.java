package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.GeneralConfigGroup;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.beans.PropertyDescriptor;
import java.util.Set;

/**
 * @author lmiky
 */
public class ValueComplexResolvePostPrecessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware, BeanFactoryPostProcessor, PriorityOrdered {
    private int order;

    private ValueAnnotationBeanPostProcessor valueAnnotationBeanPostProcessor;
    private ValueExpressionPlaceHolderBeanFactoryPostProcessor valueExpressionPlaceHolderBeanFactoryPostProcessor;
    private InjectionDataHandler injectionDataHandler;

    public ValueComplexResolvePostPrecessor() {
        valueAnnotationBeanPostProcessor = new ValueAnnotationBeanPostProcessor();
        valueExpressionPlaceHolderBeanFactoryPostProcessor = new ValueExpressionPlaceHolderBeanFactoryPostProcessor();
        injectionDataHandler = new InjectionDataHandler();
        setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     *
     */
    public void init() {
        injectionDataHandler.init();
        valueAnnotationBeanPostProcessor.setInjectionDataHandler(injectionDataHandler);
        valueExpressionPlaceHolderBeanFactoryPostProcessor.setInjectionDataHandler(injectionDataHandler);
    }

    /**
     *
     */
    public void shutdown() {
        injectionDataHandler.shutdown();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        valueExpressionPlaceHolderBeanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return valueAnnotationBeanPostProcessor.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return valueAnnotationBeanPostProcessor.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return valueAnnotationBeanPostProcessor.postProcessPropertyValues(pvs, pds, bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return valueAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return valueAnnotationBeanPostProcessor.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        valueAnnotationBeanPostProcessor.setApplicationContext(applicationContext);
        injectionDataHandler.setApplicationContext(applicationContext);
    }

    public void setExcludeScanPackages(Set<String> excludeScanPackages) {
        valueAnnotationBeanPostProcessor.setExcludeScanPackages(excludeScanPackages);
        valueExpressionPlaceHolderBeanFactoryPostProcessor.setExcludeScanPackages(excludeScanPackages);
    }

    public void setScanPackages(Set<String> scanPackages) {
        valueAnnotationBeanPostProcessor.setScanPackages(scanPackages);
        valueExpressionPlaceHolderBeanFactoryPostProcessor.setScanPackages(scanPackages);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        valueExpressionPlaceHolderBeanFactoryPostProcessor.setOrder(order);
    }

    public void setGeneralConfigGroups(Set<GeneralConfigGroup> generalConfigGroups) {
        injectionDataHandler.setGeneralConfigGroups(generalConfigGroups);
    }

    public void setDestroyBeforeReinitialize(boolean destroyBeforeReinitialize) {
        injectionDataHandler.setDestroyBeforeReinitialize(destroyBeforeReinitialize);
    }

    public void setReinitialize(boolean reinitialize) {
        injectionDataHandler.setReinitialize(reinitialize);
    }

    public void setThreadSize(int threadSize) {
        injectionDataHandler.setThreadSize(threadSize);
    }
}
