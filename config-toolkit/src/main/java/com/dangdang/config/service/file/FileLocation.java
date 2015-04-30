package com.dangdang.config.service.file;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileLocation {

	private String file;

	private Protocol protocol;

	public FileLocation(String file, Protocol protocol) {
		super();
		this.file = file;
		this.protocol = protocol;
	}

	private static final Splitter SPLITTER = Splitter.on(':').limit(2);

	public static FileLocation fromLocation(String location) {
		Iterable<String> parts = SPLITTER.split(location);
		// default as file
		if (Iterables.size(parts) == 1) {
			return new FileLocation(location, Protocol.file);
		}

		Optional<Protocol> protocol = Enums.getIfPresent(Protocol.class, Iterables.getFirst(parts, null));

		Preconditions.checkArgument(protocol.isPresent(), "Invalid location: %s", location);

		switch (protocol.get()) {
		case file:
			return new FileLocation(Iterables.getLast(parts), protocol.get());
		case classpath:
			return new FileLocation(Iterables.getLast(parts), protocol.get());
		case http:
			return new FileLocation(location, protocol.get());
		default:
			return null;
		}
	}

	public String getFile() {
		return file;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public enum Protocol {
		file, classpath, http;
	}

}
