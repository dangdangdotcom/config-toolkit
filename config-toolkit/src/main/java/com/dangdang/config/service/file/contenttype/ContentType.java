package com.dangdang.config.service.file.contenttype;

import java.util.Map;

import com.dangdang.config.service.exception.InvalidPathException;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface ContentType {

	Map<String, String> resolve(byte[] data, String encoding) throws InvalidPathException;

}
