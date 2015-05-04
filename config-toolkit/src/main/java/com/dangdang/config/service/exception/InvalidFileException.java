package com.dangdang.config.service.exception;

/**
 * The exception that the file is invalid
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class InvalidFileException extends ConfigToolkitException {

	private static final long serialVersionUID = 1L;

	public InvalidFileException() {
		super();
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFileException(String message) {
		super(message);
	}

	public InvalidFileException(Throwable cause) {
		super(cause);
	}

}
