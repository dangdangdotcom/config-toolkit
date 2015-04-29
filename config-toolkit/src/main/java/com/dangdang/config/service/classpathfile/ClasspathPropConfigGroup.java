package com.dangdang.config.service.classpathfile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jline.internal.InputStreamReader;

import com.dangdang.config.service.ConfigGroup;
import com.google.common.collect.Maps;

/**
 * Configuration group loaded from classpath property file
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ClasspathPropConfigGroup extends ClasspathFileConfigGroup {

	private static final long serialVersionUID = 1L;

	public ClasspathPropConfigGroup(ClasspathFileConfigProfile configProfile, String file) {
		this(null, configProfile, file);
	}

	public ClasspathPropConfigGroup(ConfigGroup internalConfigGroup, ClasspathFileConfigProfile configProfile, String file) {
		super(internalConfigGroup, configProfile, file);
	}

	@Override
	protected void loadConfigs(byte[] fileData, String fileEncoding) throws UnsupportedEncodingException, IOException {
		Properties props = new Properties();
		try (Reader reader = new InputStreamReader(new ByteArrayInputStream(fileData), fileEncoding)) {
			props.load(reader);
		}

		putAll(Maps.fromProperties(props));
	}

}
