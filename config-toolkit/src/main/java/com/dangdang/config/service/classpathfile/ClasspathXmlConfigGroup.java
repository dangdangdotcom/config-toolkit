package com.dangdang.config.service.classpathfile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.dangdang.config.service.ConfigGroup;
import com.google.common.collect.Maps;

/**
 * Configuration group load from classpath xml file
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ClasspathXmlConfigGroup extends ClasspathFileConfigGroup {

	public ClasspathXmlConfigGroup(ClasspathFileConfigProfile configProfile, String file) {
		this(null, configProfile, file);
	}

	public ClasspathXmlConfigGroup(ConfigGroup internalConfigGroup, ClasspathFileConfigProfile configProfile, String file) {
		super(internalConfigGroup, configProfile, file);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void loadConfigs(byte[] fileData, String fileEncoding) throws UnsupportedEncodingException, IOException {
		Properties props = new Properties();
		try (InputStream in = new ByteArrayInputStream(fileData)) {
			props.loadFromXML(in);
		}

		putAll(Maps.fromProperties(props));
	}

}
