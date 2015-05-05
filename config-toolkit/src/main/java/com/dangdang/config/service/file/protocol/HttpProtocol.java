package com.dangdang.config.service.file.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.dangdang.config.service.exception.InvalidPathException;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileLocation;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class HttpProtocol implements Protocol {

	@Override
	public void close() throws IOException {

	}

	@Override
	public byte[] read(FileLocation location) throws InvalidPathException {
		try {
			URL url = new URL(location.getProtocol() + ":" + location.getFile());
			try (InputStream in = url.openStream()) {
				return IOUtils.toByteArray(in);
			}
		} catch (MalformedURLException e) {
			throw new InvalidPathException(e);
		} catch (IOException e) {
			throw new InvalidPathException(e);
		}
	}

	@Override
	public void watch(FileLocation location, FileConfigGroup fileConfigGroup) throws InvalidPathException {

	}

}
