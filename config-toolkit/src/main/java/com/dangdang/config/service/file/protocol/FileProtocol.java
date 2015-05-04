package com.dangdang.config.service.file.protocol;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.dangdang.config.service.exception.InvalidFileException;
import com.dangdang.config.service.file.FileLocation;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileProtocol extends LocalFileProtocol {

	@Override
	protected Path getPath(FileLocation location) throws InvalidFileException {
		return Paths.get(location.getFile());
	}

}
