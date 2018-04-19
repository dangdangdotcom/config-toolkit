package com.dangdang.config.service.util;

import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.StringUtils;

/**
 * @author lmiky
 */
public class SpringUtils {

    private SpringUtils() {

    }

    /**
     * @param key
     * @return
     */
    public static String extractPlaceHolderKey(String key) {
        if(StringUtils.isEmpty(key)) {
            return key;
        }
        if (key.startsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX) && key.endsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX)) {
            int startIndex = key.indexOf(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX) + PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX.length();
            int endIndex;
            if (key.indexOf(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR) != -1) {
                endIndex = key.indexOf(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);
            } else {
                endIndex = key.indexOf(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX);
            }
            return key.substring(startIndex, endIndex).trim();
        }
        return key;
    }
}
