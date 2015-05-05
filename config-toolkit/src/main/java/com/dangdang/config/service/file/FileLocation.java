package com.dangdang.config.service.file;

import com.dangdang.config.service.file.protocol.ProtocolNames;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileLocation {

	private String file;

	private String protocol;

	public FileLocation(String file, String protocol) {
		super();
		this.file = file;
		this.protocol = protocol;
	}

	private static final Splitter SPLITTER = Splitter.on(':').limit(2);

	public static FileLocation fromLocation(String location) {
		Iterable<String> parts = SPLITTER.split(location);
		// default as file
		if (Iterables.size(parts) == 1) {
			return new FileLocation(location, ProtocolNames.FILE);
		}

		return new FileLocation(Iterables.getLast(parts), Iterables.getFirst(parts, null).toLowerCase());
	}

	public String getFile() {
		return file;
	}

	public String getProtocol() {
		return protocol;
	}

	@Override
	public String toString() {
		return "FileLocation [file=" + file + ", protocol=" + protocol + "]";
	}

}
