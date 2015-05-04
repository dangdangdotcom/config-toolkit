package com.dangdang.config.service.file.protocol;

import java.io.Closeable;

import com.dangdang.config.service.exception.InvalidFileException;
import com.dangdang.config.service.file.FileLocation;
import com.dangdang.config.service.file.FileConfigGroup;

/**
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface Protocol extends Closeable {

	/**
	 * Read data of file
	 * 
	 * @param location
	 * @return
	 */
	byte[] read(FileLocation location) throws InvalidFileException;

	/**
	 * Register watcher for the file
	 * 
	 * @param location
	 * @param fileConfigGroup
	 */
	void watch(FileLocation location, FileConfigGroup fileConfigGroup) throws InvalidFileException;

}
