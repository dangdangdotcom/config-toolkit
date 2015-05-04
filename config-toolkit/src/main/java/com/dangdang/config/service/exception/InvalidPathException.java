package com.dangdang.config.service.exception;

/**
 * The exception that the file is invalid
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class InvalidPathException extends ConfigToolkitException {

	private static final long serialVersionUID = 1L;

	public InvalidPathException() {
		super();
	}

	public InvalidPathException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPathException(String message) {
		super(message);
	}

	public InvalidPathException(Throwable cause) {
		super(cause);
	}

}
