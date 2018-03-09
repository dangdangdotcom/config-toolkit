package com.dangdang.config.service.file.contenttype;

import com.dangdang.config.service.exception.InvalidPathException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * The XML document must have the following DOCTYPE declaration:
 * 
 * <pre>
 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
 * </pre>
 * 
 * Furthermore, the document must satisfy the properties DTD described above.
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class XmlContentType implements ContentType {

	@Override
	public Map<String, String> resolve(byte[] data, String encoding) throws InvalidPathException {
		Properties props = new Properties();
		try (InputStream in = new ByteArrayInputStream(data)) {
			props.loadFromXML(in);
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
