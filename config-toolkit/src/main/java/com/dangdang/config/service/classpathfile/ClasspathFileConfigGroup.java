package com.dangdang.config.service.classpathfile;

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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.GeneralConfigGroup;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

/**
 * Configuration group loaded from classpath files
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class ClasspathFileConfigGroup extends GeneralConfigGroup {

	private static final long serialVersionUID = 1L;

	private ClasspathFileConfigProfile configProfile;

	private String file;

	private WatchService watcher;

	static final Logger LOGGER = LoggerFactory.getLogger(ClasspathFileConfigGroup.class);

	protected ClasspathFileConfigGroup(ConfigGroup internalConfigGroup, ClasspathFileConfigProfile configProfile, String file) {
		super(internalConfigGroup);
		this.configProfile = configProfile;
		this.file = file;
		initConfigs();
	}

	private void initConfigs() {
		String filePath = getVersionedFile();
		LOGGER.debug("Loading classpath file: {}", filePath);
		try {
			URL fileUrl = Resources.getResource(filePath);
			Path path = Paths.get(fileUrl.toURI());

			Preconditions.checkArgument(Files.isReadable(path), "The file is not readable.");
			loadConfigs(Files.readAllBytes(path), Objects.firstNonNull(configProfile.getFileEncoding(), Charsets.UTF_8.name()));

			// Register file change listener
			watcher = FileSystems.getDefault().newWatchService();
			path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			new Thread(new FileChangeEventListener(watcher, this)).start();
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private String getVersionedFile() {
		if (Strings.isNullOrEmpty(configProfile.getVersion())) {
			return file;
		}
		return FilenameUtils.concat(configProfile.getVersion(), file);
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

}
