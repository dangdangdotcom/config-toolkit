package com.dangdang.config.service.file.protocol;

import com.dangdang.config.service.exception.InvalidPathException;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
				try(ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = in.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();
					return buffer.toByteArray();
				}
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
