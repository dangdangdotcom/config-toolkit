package com.dangdang.config.service.file.protocol;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

/**
 * Protocol factory
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public final class Protocols {

	private Map<String, Class<Protocol>> protocols;

	private static Protocols INSTANCE = new Protocols();

	private static final String REGISTER_FILE = "META-INF/toolkit/com.dangdang.config.service.file.protocol.Protocol";

	private Protocols() {
		try {
			Properties props = new Properties();

			// Load register file contents
			Enumeration<URL> registerFiles = this.getClass().getClassLoader().getResources(REGISTER_FILE);
			URL registerFile = null;
			while (registerFiles.hasMoreElements()) {
				registerFile = registerFiles.nextElement();
				try (InputStream in = registerFile.openStream()) {
					props.load(in);
				}
			}

			// Initialize protocol beans
			protocols = Maps.newHashMap();
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				final String protocolName = ((String) entry.getKey()).toLowerCase();
				@SuppressWarnings("unchecked")
				final Class<Protocol> protocolBeanClazz = (Class<Protocol>) Class.forName((String) entry.getValue());
				protocols.put(protocolName, protocolBeanClazz);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static Protocols getInstance() {
		return INSTANCE;
	}

	public Class<Protocol> get(String protocolName) {
		return Preconditions.checkNotNull(protocols.get(protocolName), "Procotol with name {} not registered.", protocolName);
	}

}
