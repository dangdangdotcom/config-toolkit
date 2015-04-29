package com.dangdang.config.service.classpathfile;

import com.dangdang.config.service.ConfigProfile;

/**
 * Classpath file configuration profile
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ClasspathFileConfigProfile extends ConfigProfile {

	private String fileEncoding;

	public ClasspathFileConfigProfile() {
		this(null);
	}

	public ClasspathFileConfigProfile(String version) {
		super(version);
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

}
