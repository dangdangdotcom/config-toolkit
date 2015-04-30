package com.dangdang.config.service.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.dangdang.config.service.ConfigGroup;
import com.google.common.collect.Maps;

/**
 * Configuration group load from xml file
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class XmlFileConfigGroup extends URIConfigGroup {

	public XmlFileConfigGroup(FileConfigProfile configProfile, String location) {
		this(null, configProfile, location);
	}

	public XmlFileConfigGroup(ConfigGroup internalConfigGroup, FileConfigProfile configProfile, String location) {
		super(internalConfigGroup, configProfile, location);
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
