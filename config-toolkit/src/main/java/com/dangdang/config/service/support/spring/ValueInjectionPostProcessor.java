package com.dangdang.config.service.support.spring;

import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author lmiky
 * @date 2018/4/11 11:29
 */
public abstract class ValueInjectionPostProcessor {

    /**
     * @param bean
     * @return
     */
    protected boolean isScan(Object bean) {
        return bean != null && isScan(bean.getClass().getName());
    }

    /**
     * 是否在扫描返回内
     * @param beanClassName
     * @return
     */
    protected boolean isScan(String beanClassName) {
        if(StringUtils.isEmpty(beanClassName)) {
            return false;
        }
        Set<String> excludeScanPackages = getExcludeScanPackages();
        if(excludeScanPackages != null && !excludeScanPackages.isEmpty()) {
            for(String packageName : excludeScanPackages) {
                if(beanClassName.startsWith(packageName)) {
                    return false;
                }
            }
        }
        Set<String> scanPackages = getScanPackages();
        if(scanPackages != null && !scanPackages.isEmpty()) {
            for (String packageName : scanPackages) {
                if (beanClassName.startsWith(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    protected abstract Set<String> getExcludeScanPackages();

    /**
     *
     * @return
     */
    protected abstract Set<String> getScanPackages();
}
