package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.util.SpringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author lmiky
 */
public class ValueAnnotationBeanPostProcessor extends ValueInjectionPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValueAnnotationBeanPostProcessor.class);
    private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
    private InjectionDataHandler injectionDataHandler;
    private Set<String> excludeScanPackages;
    private Set<String> scanPackages;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        if(autowiredAnnotationBeanPostProcessor == null) {
            throw new UnsupportedOperationException("ValueAnnotationBeanPostProcessor no autowiredAnnotationBeanPostProcessor to process!");
        }
        if(!isScan(bean)) {
            return pvs;
        }
        Method method = ReflectionUtils.findMethod(AutowiredAnnotationBeanPostProcessor.class, "findAutowiringMetadata", null);
        if(method == null) {
            throw new UnsupportedOperationException("get AutowiredAnnotationBeanPostProcessor [findAutowiringMetadata] Method error, maybe unsupport Spring version!");
        }
        ReflectionUtils.makeAccessible(method);
        InjectionMetadata injectionMetadata;
        try {
            injectionMetadata = (InjectionMetadata) method.invoke(autowiredAnnotationBeanPostProcessor, beanName, bean.getClass(), pvs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException("invoke autowiredAnnotationBeanPostProcessor [findAutowiringMetadata] error, maybe unsupport Spring version!", e);
        }
        if (injectionMetadata == null) {
            return pvs;
        }
        Field injectedElementsField = ReflectionUtils.findField(InjectionMetadata.class, "injectedElements");
        if(injectedElementsField == null) {
            throw new UnsupportedOperationException("invoke InjectionMetadata field[injectedElements] error, maybe unsupport Spring version!");
        }
        ReflectionUtils.makeAccessible(injectedElementsField);
        Collection<InjectionMetadata.InjectedElement> injectedElements;
        try {
            injectedElements = (Collection<InjectionMetadata.InjectedElement>)injectedElementsField.get(injectionMetadata);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("get InjectionMetadata field[injectedElements] value error, maybe unsupport Spring version!", e);
        }
        if(injectedElements == null || injectedElements.isEmpty()) {
            return pvs;
        }
        for(InjectionMetadata.InjectedElement injectedElement : injectedElements) {
            Member member = injectedElement.getMember();
            if(member instanceof Field) {
                Field memberField = (Field) member;
                if(addToInjectMonitor(injectedElement, memberField, beanName, bean)) {
                    LOGGER.info("add bean[{}] MetaDate field[{}] to InjectionHandler!", beanName, memberField.getName());
                }
            } else if(member instanceof Method) {
                Method memberMethod = (Method) member;
                if(addToInjectMonitor(injectedElement, memberMethod, beanName, bean)) {
                    LOGGER.info("add bean[{}] MetaDate method[{}] to InjectionHandler!", beanName, memberMethod.getName());
                }
            }
        }
        return pvs;
    }

    /**
     * @param injectedElement
     * @param accessibleObject
     * @param beanName
     * @param bean
     * @return
     */
    public boolean addToInjectMonitor(InjectionMetadata.InjectedElement injectedElement, AccessibleObject accessibleObject, String beanName, Object bean) {
        if(accessibleObject.getAnnotation(Value.class) != null) {
            Value valueAnnotation = accessibleObject.getAnnotation(Value.class);
            String annoValue = valueAnnotation.value();
            if(StringUtils.isEmpty(annoValue)) {
                return false;
            }
            String placeHolderKey = SpringUtils.extractPlaceHolderKey(annoValue);
            injectionDataHandler.addInjection(placeHolderKey, new BeanInjectionMetaData(beanName, new InjectionMetadata(bean.getClass(), Lists.newArrayList(injectedElement))));
            return true;
        }
        return false;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            Map<String, AutowiredAnnotationBeanPostProcessor> beans = applicationContext.getBeansOfType(AutowiredAnnotationBeanPostProcessor.class);
            if (beans == null || beans.isEmpty()) {
                if(applicationContext.containsBean(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
                    autowiredAnnotationBeanPostProcessor = applicationContext.getBean(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, AutowiredAnnotationBeanPostProcessor.class);
                } else {
                    autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
                    autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationType(Value.class);
                }
            } else {
                autowiredAnnotationBeanPostProcessor = beans.entrySet().iterator().next().getValue();
            }
        } catch (Exception e) {
            LOGGER.warn("create AutowiredAnnotationBeanPostProcessor error!", e);
            autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
            autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationType(Value.class);
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
}
