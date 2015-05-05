package com.dangdang.config.service.file.contenttype;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import jline.internal.InputStreamReader;

import com.dangdang.config.service.exception.InvalidPathException;
import com.google.common.collect.Maps;

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

		return Maps.fromProperties(props);
	}

}
