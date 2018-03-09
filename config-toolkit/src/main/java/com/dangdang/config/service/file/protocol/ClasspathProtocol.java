package com.dangdang.config.service.file.protocol;

import com.dangdang.config.service.exception.InvalidPathException;
import com.dangdang.config.service.file.FileLocation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ClasspathProtocol extends LocalFileProtocol {

	private static Map<String, FileSystem> fsMap;

	static {
		fsMap = new HashMap<>();
	}

	@Override
	protected Path getPath(FileLocation location) throws InvalidPathException {
		try {
			URL url = this.getClass().getClassLoader().getResource(location.getFile());
			// 兼容spring-boot 1.4.4
			if (url.getPath().contains("/BOOT-INF/classes!")) {
				URI uri = url.toURI();
				final Map<String, String> env = new HashMap<>();
				final String[] array = uri.toString().split("!");
				FileSystem fs;
				if (!fsMap.containsKey(array[0])) {
					fsMap.put(array[0], FileSystems.newFileSystem(URI.create(array[0]), env));
				}
				fs = fsMap.get(array[0]);
				return fs.getPath(array[1], array[2]);
			}
			return Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			throw new InvalidPathException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
