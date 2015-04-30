package com.dangdang.config.service.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jline.internal.InputStreamReader;

import com.dangdang.config.service.ConfigGroup;
import com.google.common.collect.Maps;

/**
 * Configuration group loaded from property file
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class PropFileConfigGroup extends URIConfigGroup {

	private static final long serialVersionUID = 1L;

	public PropFileConfigGroup(FileConfigProfile configProfile, String location) {
		this(null, configProfile, location);
	}

	public PropFileConfigGroup(ConfigGroup internalConfigGroup, FileConfigProfile configProfile, String location) {
		super(internalConfigGroup, configProfile, location);
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
