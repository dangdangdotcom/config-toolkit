package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.observer.IObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lmiky
 */
public class InjectionDataHandler implements ApplicationContextAware {
    protected final static Logger logger = LoggerFactory.getLogger(InjectionDataHandler.class);
    private static Method NON_METHOD = ReflectionUtils.findMethod(InjectionDataHandler.class, "init");;

    private int threadSize = Runtime.getRuntime().availableProcessors() + 1;
    private ConcurrentHashMap<String, List<BeanInjectionMetaData>> injectionMetaDataCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<BeanInjectionFieldData>> injectionFieldDataCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, RootBeanDefinition> rootBeanDefinitionCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Method> initMethodCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Method> destroyMethodCache = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;
    private DefaultListableBeanFactory defaultListableBeanFactory;
    private Set<GeneralConfigGroup> generalConfigGroups;
    private ExecutorService executorService;
    private boolean destroyBeforeReinitialize;
    private boolean reinitialize;

    /**
     * init
     */
    public void init() {
        if(reinitialize && defaultListableBeanFactory == null) {
            throw new UnsupportedOperationException("DefaultListableBeanFactory must not be null if you config reinitialize!");
        }
        executorService = Executors.newFixedThreadPool(threadSize);
        for(GeneralConfigGroup generalConfigGroup : generalConfigGroups) {
            generalConfigGroup.register(new IObserver() {
                @Override
                public void notified(String data, String value) {
                    String newValue = tidyValue(data, value);
                    logger.info("receive InjectionData change event: data:[{}], value:[{}]", data, newValue);
                    handle(data, newValue);
                }
            });
        }
    }

    /**
     * @param key
     * @param value
     * @return
     */
    protected String tidyValue(String key, String value) {
        return value;
    }

    /**
     * 添加注入信息
     * @param key
     * @param beanInjectionMetaData
     */
    public void addInjection(String key, BeanInjectionMetaData beanInjectionMetaData) {
        if(StringUtils.isEmpty(key) || beanInjectionMetaData == null) {
            return;
        }
        if(!injectionMetaDataCache.containsKey(key)) {
            injectionMetaDataCache.putIfAbsent(key, Collections.synchronizedList(new ArrayList<BeanInjectionMetaData>()));
        }
        injectionMetaDataCache.get(key).add(beanInjectionMetaData);
    }

    /**
     * 添加注入信息
     * @param key
     * @param beanInjectionFieldData
     */
    public void addInjection(String key, BeanInjectionFieldData beanInjectionFieldData) {
        if(StringUtils.isEmpty(key) || beanInjectionFieldData == null) {
            return;
        }
        if(!injectionFieldDataCache.containsKey(key)) {
            injectionFieldDataCache.putIfAbsent(key, Collections.synchronizedList(new ArrayList<BeanInjectionFieldData>()));
        }
        injectionFieldDataCache.get(key).add(beanInjectionFieldData);
    }

    /**
     * 处理
     * @param key
     * @param value
     * @author lmiky
     * @date 2018/4/9 17:56
     */
    public void handle(String key, String value) {
        if(StringUtils.isEmpty(key) || (!injectionMetaDataCache.containsKey(key) && !injectionFieldDataCache.containsKey(key))) {
            return;
        }
        if(injectionMetaDataCache.containsKey(key)) {
            for (BeanInjectionMetaData injection : injectionMetaDataCache.get(key)) {
                submitInjectMetaDataTask(key, value, injection);
            }
        }
        if(injectionFieldDataCache.containsKey(key)) {
            for (BeanInjectionFieldData injection : injectionFieldDataCache.get(key)) {
                submitInjectFieldDataTask(key, value, injection);
            }
        }
    }

    /**
     * 提交任务
     * @param key
     * @param value
     * @param beanInjectionMetaData
     */
    private void submitInjectMetaDataTask(final String key, final String value, final BeanInjectionMetaData beanInjectionMetaData) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String beanName = beanInjectionMetaData.getBeanName();
                if (!applicationContext.containsBean(beanName)) {
                    logger.error("unknown bean[{}] to inject MetaData!", beanName);
                    return;
                }
                Object bean = applicationContext.getBean(beanInjectionMetaData.getBeanName());
                InjectionMetadata injectionMetadata = beanInjectionMetaData.getInjectionMetadata();
                try {
                    injectionMetadata.inject(bean, key, null);
                    reinitialize(bean, beanName);
                } catch (Throwable throwable) {
                    logger.error(String.format("handle InjectionMetadata[key: %s, beanName: %s] error!", key, beanName), throwable);
                }
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param beanInjectionFieldData
     */
    private void submitInjectFieldDataTask(final String key, final String value, final BeanInjectionFieldData beanInjectionFieldData) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String beanName = beanInjectionFieldData.getBeanName();
                if (!applicationContext.containsBean(beanName)) {
                    logger.error("unknown bean[{}] to inject FieldData!", beanName);
                    return;
                }
                Object bean = applicationContext.getBean(beanInjectionFieldData.getBeanName());
                BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
                beanWrapper.setPropertyValue(beanInjectionFieldData.getBeanField(), value);
                reinitialize(bean, beanName);
            }
        });
    }

    /**
     * @param bean
     * @param beanName
     */
    private void reinitialize(Object bean, String beanName) {
        if(!reinitialize) {
            return;
        }
        RootBeanDefinition rootBeanDefinition;
        if(!rootBeanDefinitionCache.containsKey(beanName)) {
            Method method = ReflectionUtils.findMethod(DefaultListableBeanFactory.class, "getMergedLocalBeanDefinition", String.class);
            if (method == null) {
                throw new UnsupportedOperationException("get DefaultListableBeanFactory [getMergedLocalBeanDefinition] Method error, maybe unsupport Spring version!");
            }
            ReflectionUtils.makeAccessible(method);
            try {
                rootBeanDefinition = (RootBeanDefinition) method.invoke(defaultListableBeanFactory, beanName);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new UnsupportedOperationException("invoke DefaultListableBeanFactory [getMergedLocalBeanDefinition] error, maybe unsupport Spring version!", e);
            }
            if(rootBeanDefinition == null) {
                throw new UnsupportedOperationException("invoke DefaultListableBeanFactory [getMergedLocalBeanDefinition] error, maybe unsupport Spring version!");
            }
            rootBeanDefinitionCache.putIfAbsent(beanName, rootBeanDefinition);
        }
        rootBeanDefinition = rootBeanDefinitionCache.get(beanName);
        invokeBeanDestroyMethod(bean, beanName, rootBeanDefinition);
        invokeBeanInitMethod(bean, beanName, rootBeanDefinition);
    }


    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     */
    private void invokeBeanInitMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition) {
        Method initMethod;
        if(!initMethodCache.containsKey(beanName)) {
            initMethodCache.putIfAbsent(beanName, extractInitMethod(bean, beanName, rootBeanDefinition));
        }
        initMethod = initMethodCache.get(beanName);
        if(initMethod == NON_METHOD) {
            return;
        }
        try {
            if(beforeInvokeInitMethod(bean, beanName, rootBeanDefinition, initMethod)) {
                initMethod.invoke(bean);
                afterInvokeInitMethod(bean, beanName, rootBeanDefinition, initMethod);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException(String.format("invoke beanName[%s] init method error!", beanName), e);
        }
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @return
     */
    protected Method extractInitMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition) {
        String initMethodName = rootBeanDefinition.getInitMethodName();
        Method initMethod = null;
        if (StringUtils.isEmpty(initMethodName)) {
            final List<Method> methods = new ArrayList<>();
            ReflectionUtils.doWithLocalMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    if (method.getAnnotation(PostConstruct.class) != null) {
                        methods.add(method);
                    }
                }
            });
            if(!methods.isEmpty()) {
                initMethod = methods.get(0);
            }
        } else {
            initMethod = (rootBeanDefinition.isNonPublicAccessAllowed() ?
                    BeanUtils.findMethod(bean.getClass(), initMethodName) :
                    ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName));
        }
        if (initMethod == null) {
            initMethod = NON_METHOD;
        } else {
            ReflectionUtils.makeAccessible(initMethod);
        }
        return initMethod;
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     */
    private void invokeBeanDestroyMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition) {
        if(!destroyBeforeReinitialize) {
            return;
        }
        Method destroyMethod;
        if(!destroyMethodCache.containsKey(beanName)) {
            destroyMethodCache.putIfAbsent(beanName, extractDestoryMethod(bean, beanName, rootBeanDefinition));
        }
        destroyMethod = destroyMethodCache.get(beanName);
        if(destroyMethod == NON_METHOD) {
            return;
        }
        try {
            if(beforeInvokeDestroyMethod(bean, beanName, rootBeanDefinition, destroyMethod)) {
                destroyMethod.invoke(bean);
                afterInvokeDestroyMethod(bean, beanName, rootBeanDefinition, destroyMethod);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException(String.format("invoke beanName[%s] destroy method error!", beanName), e);
        }
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @return
     */
    protected Method extractDestoryMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition) {
        String destroyMethodName = rootBeanDefinition.getDestroyMethodName();
        Method destroyMethod = null;
        if (StringUtils.isEmpty(destroyMethodName)) {
            final List<Method> methods = new ArrayList<>();
            ReflectionUtils.doWithLocalMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    if (method.getAnnotation(PreDestroy.class) != null) {
                        methods.add(method);
                    }
                }
            });
            if(!methods.isEmpty()) {
                destroyMethod = methods.get(0);
            }
        } else {
            destroyMethod = (rootBeanDefinition.isNonPublicAccessAllowed() ?
                    BeanUtils.findMethod(bean.getClass(), destroyMethodName) :
                    ClassUtils.getMethodIfAvailable(bean.getClass(), destroyMethodName));
        }
        if (destroyMethod == null) {
            destroyMethod = NON_METHOD;
        } else {
            ReflectionUtils.makeAccessible(destroyMethod);
        }
        return destroyMethod;
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @param initMethod
     * @Return
     */
    protected boolean beforeInvokeInitMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition, Method initMethod) {
        return true;
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @param initMethod
     */
    protected void afterInvokeInitMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition, Method initMethod) {

    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @param destroyMethod
     * @Return
     */
    protected boolean beforeInvokeDestroyMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition, Method destroyMethod) {
        return true;
    }

    /**
     * @param bean
     * @param beanName
     * @param rootBeanDefinition
     * @param destroyMethod
     */
    protected void afterInvokeDestroyMethod(Object bean, String beanName, RootBeanDefinition rootBeanDefinition, Method destroyMethod) {

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        if(autowireCapableBeanFactory != null && autowireCapableBeanFactory instanceof DefaultListableBeanFactory) {
            defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
        }
    }

    /**
     * shutdown
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public Set<GeneralConfigGroup> getGeneralConfigGroups() {
        return generalConfigGroups;
    }

    public void setGeneralConfigGroups(Set<GeneralConfigGroup> generalConfigGroups) {
        this.generalConfigGroups = generalConfigGroups;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public boolean isDestroyBeforeReinitialize() {
        return destroyBeforeReinitialize;
    }

    public void setDestroyBeforeReinitialize(boolean destroyBeforeReinitialize) {
        this.destroyBeforeReinitialize = destroyBeforeReinitialize;
    }

    public boolean isReinitialize() {
        return reinitialize;
    }

    public void setReinitialize(boolean reinitialize) {
        this.reinitialize = reinitialize;
    }

}
