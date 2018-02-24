package com.dangdang.config.service.support.spring;

import com.dangdang.config.service.ConfigGroup;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Spring Enumerable Property Sources support
 *
 * @author <a href="mailto:shiqingshun@gmail.com">Qingshun Shi</a>
 */
public class ConfigGroupEnumerableResource extends EnumerablePropertySource<ConfigGroup> implements Closeable {

    public ConfigGroupEnumerableResource(ConfigGroup configNode) {
        super(UUID.randomUUID().toString(), configNode);
    }

    @Override
    public Object getProperty(String name) {
        return super.getSource().get(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Closeable#close()
     */
    @Override
    @PreDestroy
    public void close() throws IOException {
        super.getSource().close();
    }

    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((Map)this.source).keySet());
    }
}