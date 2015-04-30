package com.dangdang.config.service.file;

import com.dangdang.config.service.ConfigProfile;

/**
 * File configuration profile
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileConfigProfile extends ConfigProfile {

	private String fileEncoding;

	public FileConfigProfile() {
		this(null);
	}

	public FileConfigProfile(String version) {
		super(version);
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

}
