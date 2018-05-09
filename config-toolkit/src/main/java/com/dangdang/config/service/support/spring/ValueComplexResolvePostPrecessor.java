package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.GeneralConfigGroup;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.DisposableBean;
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
public class ValueComplexResolvePostPrecessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware, BeanFactoryPostProcessor, DisposableBean, PriorityOrdered {
    private int order = Ordered.HIGHEST_PRECEDENCE;

    private volatile ValueAnnotationBeanPostProcessor valueAnnotationBeanPostProcessor;
    private volatile ValueExpressionPlaceHolderBeanFactoryPostProcessor valueExpressionPlaceHolderBeanFactoryPostProcessor;
    private volatile InjectionDataHandler injectionDataHandler;
    private ApplicationContext applicationContext;

    private Set<String> excludeScanPackages;
    private Set<String> scanPackages;

    private Set<GeneralConfigGroup> generalConfigGroups;
    private Integer threadSize;
    private boolean destroyBeforeReinitialize;
    private boolean reinitialize;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        getValueExpressionPlaceHolderProcessor().postProcessBeanFactory(beanFactory);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return getValueAnnotationProcessor().postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return getValueAnnotationProcessor().postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return getValueAnnotationProcessor().postProcessPropertyValues(pvs, pds, bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return getValueAnnotationProcessor().postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return getValueAnnotationProcessor().postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @return
     */
    public ValueExpressionPlaceHolderBeanFactoryPostProcessor getValueExpressionPlaceHolderProcessor() {
        if(valueExpressionPlaceHolderBeanFactoryPostProcessor == null) {
            synchronized (this) {
                if(valueExpressionPlaceHolderBeanFactoryPostProcessor == null) {
                    valueExpressionPlaceHolderBeanFactoryPostProcessor = new ValueExpressionPlaceHolderBeanFactoryPostProcessor();
                    valueExpressionPlaceHolderBeanFactoryPostProcessor.setOrder(order);
                    if(excludeScanPackages != null) {
                        valueExpressionPlaceHolderBeanFactoryPostProcessor.setExcludeScanPackages(excludeScanPackages);
                    }
                    if(scanPackages != null) {
                        valueExpressionPlaceHolderBeanFactoryPostProcessor.setScanPackages(scanPackages);
                    }
                    valueExpressionPlaceHolderBeanFactoryPostProcessor.setInjectionDataHandler(getInjectionDataHandler());
                }
            }
        }
        return valueExpressionPlaceHolderBeanFactoryPostProcessor;
    }

    /**
     *
     */
    public ValueAnnotationBeanPostProcessor getValueAnnotationProcessor() {
        if(valueAnnotationBeanPostProcessor == null) {
            synchronized (this) {
                if(valueAnnotationBeanPostProcessor == null) {
                    valueAnnotationBeanPostProcessor = new ValueAnnotationBeanPostProcessor();
                    if(excludeScanPackages != null) {
                        valueAnnotationBeanPostProcessor.setExcludeScanPackages(excludeScanPackages);
                    }
                    if(scanPackages != null) {
                        valueAnnotationBeanPostProcessor.setScanPackages(scanPackages);
                    }
                    valueAnnotationBeanPostProcessor.setApplicationContext(applicationContext);
                    valueAnnotationBeanPostProcessor.setInjectionDataHandler(getInjectionDataHandler());
                }
            }
        }
        return valueAnnotationBeanPostProcessor;
    }

    /**
     * @return
     */
    public InjectionDataHandler getInjectionDataHandler() {
        if(injectionDataHandler == null) {
            synchronized (this) {
                if(injectionDataHandler == null) {
                    injectionDataHandler = new InjectionDataHandler();
                    injectionDataHandler.setDestroyBeforeReinitialize(destroyBeforeReinitialize);
                    injectionDataHandler.setReinitialize(reinitialize);
                    injectionDataHandler.setGeneralConfigGroups(generalConfigGroups);
                    if(threadSize != null) {
                        injectionDataHandler.setThreadSize(threadSize);
                    }
                    injectionDataHandler.setApplicationContext(applicationContext);
                    injectionDataHandler.init();
                }
            }
        }
        return injectionDataHandler;
    }

    @Override
    public void destroy() throws Exception {
        if(injectionDataHandler != null) {
            injectionDataHandler.shutdown();
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setExcludeScanPackages(Set<String> excludeScanPackages) {
        this.excludeScanPackages = excludeScanPackages;
    }

    public void setScanPackages(Set<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setGeneralConfigGroups(Set<GeneralConfigGroup> generalConfigGroups) {
        this.generalConfigGroups = generalConfigGroups;
    }

    public void setDestroyBeforeReinitialize(boolean destroyBeforeReinitialize) {
        this.destroyBeforeReinitialize = destroyBeforeReinitialize;
    }

    public void setReinitialize(boolean reinitialize) {
        this.reinitialize = reinitialize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }
}