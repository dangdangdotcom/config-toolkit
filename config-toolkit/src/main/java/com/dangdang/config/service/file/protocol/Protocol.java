package com.dangdang.config.service.file.protocol;

import java.io.Closeable;

import com.dangdang.config.service.exception.InvalidPathException;
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
	byte[] read(FileLocation location) throws InvalidPathException;

	/**
	 * Register watcher for the file
	 * 
	 * @param location
	 * @param fileConfigGroup
	 */
	void watch(FileLocation location, FileConfigGroup fileConfigGroup) throws InvalidPathException;

}
