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

	private String contentType;
	
	public FileConfigProfile(String fileEncoding, String contentType) {
		this(null, fileEncoding, contentType);
	}

	public FileConfigProfile(String version, String fileEncoding, String contentType) {
		super(version);
		this.fileEncoding = fileEncoding;
		this.contentType = contentType;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "FileConfigProfile [fileEncoding=" + fileEncoding + ", contentType=" + contentType + "]";
	}

}
