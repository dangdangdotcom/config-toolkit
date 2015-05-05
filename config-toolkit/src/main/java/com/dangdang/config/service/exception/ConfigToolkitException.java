package com.dangdang.config.service.exception;

/**
 * Config toolkit root exception
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ConfigToolkitException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConfigToolkitException() {
		super();
	}

	public ConfigToolkitException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigToolkitException(String message) {
		super(message);
	}

	public ConfigToolkitException(Throwable cause) {
		super(cause);
	}

}
