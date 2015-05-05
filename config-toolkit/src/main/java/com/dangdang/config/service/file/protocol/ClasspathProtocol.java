package com.dangdang.config.service.file.protocol;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.dangdang.config.service.exception.InvalidPathException;
import com.dangdang.config.service.file.FileLocation;
import com.google.common.io.Resources;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ClasspathProtocol extends LocalFileProtocol {

	@Override
	protected Path getPath(FileLocation location) throws InvalidPathException {
		try {
			return Paths.get(Resources.getResource(location.getFile()).toURI());
		} catch (URISyntaxException e) {
			throw new InvalidPathException(e);
		}
	}

}
