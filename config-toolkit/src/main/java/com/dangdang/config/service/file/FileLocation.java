package com.dangdang.config.service.file;

import com.dangdang.config.service.file.protocol.ProtocolNames;

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

	private static final String COLON = ":";

	public static FileLocation fromLocation(String location) {
		// default as file
		if (!location.contains(COLON)) {
			return new FileLocation(location, ProtocolNames.FILE);
		}

		final int i = location.indexOf(COLON);
		final String protocol = location.substring(0, i);
		final String file = location.substring(i + 1);

		return new FileLocation(file, protocol.toLowerCase());
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
