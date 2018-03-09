package com.dangdang.config.service.file.contenttype;

import com.dangdang.config.service.exception.InvalidPathException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class PropertiesContentType implements ContentType {

	@Override
	public Map<String, String> resolve(byte[] data, String encoding) throws InvalidPathException {
		Properties props = new Properties();
		try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data), encoding)) {
			props.load(reader);
		} catch (IOException e) {
			throw new InvalidPathException(e);
		}

		final HashMap<String, String> propMap = new HashMap<>();
		for(String key : props.stringPropertyNames()) {
			propMap.put(key, props.getProperty(key));
		}
		return propMap;
	}

}
