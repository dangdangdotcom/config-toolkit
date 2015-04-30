package com.dangdang.config.service.file;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.file.FileLocation.Protocol;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

/**
 * Configuration group loaded from URI location.<br>
 * <br>
 * Supported location formats:<br>
 * 
 * <ul>
 * <li>classpath:config.properties</li>
 * <li>classpath:config.xml</li>
 * <li>file:/your/path/config.properties</li>
 * <li>file:/your/path/config.xml</li>
 * <li>http://www.yoursite.com/config.properties</li>
 * <li>http://www.yoursite.com/config.xml</li>
 * <ul>
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class URIConfigGroup extends GeneralConfigGroup {

	private static final long serialVersionUID = 1L;

	private FileConfigProfile configProfile;

	private FileLocation location;

	private WatchService watcher;

	private Path watchedFile;

	static final Logger LOGGER = LoggerFactory.getLogger(URIConfigGroup.class);

	protected URIConfigGroup(ConfigGroup internalConfigGroup, FileConfigProfile configProfile, String location) {
		super(internalConfigGroup);
		this.configProfile = configProfile;
		this.location = FileLocation.fromLocation(Preconditions.checkNotNull(location, "Location cannot be null."));
		initConfigs();
	}

	protected void initConfigs() {
		String filePath = location.getFile();
		LOGGER.debug("Loading file: {}", filePath);
		try {
			if (location.getProtocol() == Protocol.classpath) {
				URL fileUrl = Resources.getResource(filePath);
				watchedFile = Paths.get(fileUrl.toURI());
			} else if (location.getProtocol() == Protocol.file) {
				watchedFile = Paths.get(filePath);
			}

			Preconditions.checkArgument(Files.isReadable(watchedFile), "The file is not readable.");
			loadConfigs(Files.readAllBytes(watchedFile), Objects.firstNonNull(configProfile.getFileEncoding(), Charsets.UTF_8.name()));

			// Register file change listener
			watcher = FileSystems.getDefault().newWatchService();
			watchedFile.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			new Thread(new FileChangeEventListener(watcher, this)).start();
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	protected abstract void loadConfigs(byte[] fileData, String fileEncoding) throws UnsupportedEncodingException, IOException;

	@Override
	public void destroy() {
		if (watcher != null) {
			try {
				watcher.close();
			} catch (IOException e) {
			}
		}
	}

	Path getWatchedFile() {
		return watchedFile;
	}

}
