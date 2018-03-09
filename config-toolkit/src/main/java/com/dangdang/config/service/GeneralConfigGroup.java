package com.dangdang.config.service;

import com.dangdang.config.service.observer.IObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 */
public abstract class GeneralConfigGroup extends ConcurrentHashMap<String, String> implements ConfigGroup {

    private static final long serialVersionUID = 1L;

    private ConfigGroup internalConfigGroup;

    /**
     * 兼容spring,是否通过EnumerablePropertySource加载配置组
     */
    protected boolean enumerable = false;

    protected GeneralConfigGroup(ConfigGroup internalConfigGroup) {
        this.internalConfigGroup = internalConfigGroup;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralConfigGroup.class);

    /**
     * 配置组的最后加载时间
     */
    private long lastLoadTime;

    public long getLastLoadTime() {
        return lastLoadTime;
    }

    @Override
    public final String get(String key) {
        String val = super.get(key);
        if (val == null && internalConfigGroup != null) {
            val = internalConfigGroup.get(key);
        }
        return val;
    }

    @Override
    public final String get(Object key) {
        return get(key.toString());
    }

    protected final void cleanAndPutAll(Map<? extends String, ? extends String> configs) {
        lastLoadTime = System.currentTimeMillis();
        if (configs != null && configs.size() > 0) {
            // clear
            if (this.size() > 0) {
                for (String key : new HashSet<>(this.keySet())) {
                    if (!configs.containsKey(key)) {
                        super.remove(key);
                    }
                }
            }

            // update
            for (Map.Entry<? extends String, ? extends String> entry : configs.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }

        } else {
            LOGGER.debug("Config group has none keys, clear.");
            super.clear();
        }
    }

    @Override
    public final String put(String key, String value) {
        if (value != null) {
            value = value.trim();
        }
        String preValue = super.get(key);
        if (value != null && !value.equals(preValue)) {
            LOGGER.debug("Key " + key + " change from {} to {}", preValue, value);
            super.put(key, value);

            // If value change, notify
            if (preValue != null) {
                notify(key, value);
            }
        }
        return preValue;
    }

    /**
     * 观察者列表
     */
    private final List<IObserver> watchers = new ArrayList<>();

    @Override
    public void register(final IObserver watcher) {
        if(watcher == null) {
            throw new IllegalArgumentException("watcher cannot be null");
        }
        watchers.add(watcher);
    }

    @Override
    public void notify(final String key, final String value) {
        for (final IObserver watcher : watchers) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    watcher.notified(key, value);
                }
            }).start();
        }
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnumerable() {
        return this.enumerable;
    }
}
