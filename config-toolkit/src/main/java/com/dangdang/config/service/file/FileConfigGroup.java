package com.dangdang.config.service.file;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.exception.InvalidPathException;
import com.dangdang.config.service.file.contenttype.ContentType;
import com.dangdang.config.service.file.contenttype.ContentTypes;
import com.dangdang.config.service.file.protocol.Protocol;
import com.dangdang.config.service.file.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
 * </ul>
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileConfigGroup extends GeneralConfigGroup {

	private static final long serialVersionUID = 1L;

	private FileConfigProfile configProfile;

	private FileLocation location;

	private Protocol protocolBean;

	private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigGroup.class);

	public FileConfigGroup(FileConfigProfile configProfile, String location, boolean enumerable) {
		this(configProfile, location);
		super.enumerable = enumerable;
	}

	public FileConfigGroup(FileConfigProfile configProfile, String location) {
		this(null, configProfile, location);
	}

	public FileConfigGroup(ConfigGroup internalConfigGroup, FileConfigProfile configProfile, String location) {
		super(internalConfigGroup);

		if(location == null) {
			throw new IllegalArgumentException("Location cannot be null.");
		}

		this.configProfile = configProfile;
		this.location = FileLocation.fromLocation(location);
		initConfigs();
		try {
			protocolBean.watch(this.location, this);
		} catch (InvalidPathException e) {
			throw new RuntimeException(e);
		}
	}

	protected void initConfigs() {
		LOGGER.debug("Loading file: {}", location);
		try {
			protocolBean = Protocols.getInstance().get(location.getProtocol()).newInstance();

			ContentType contentTypeBean = ContentTypes.getInstance().get(configProfile.getContentType()).newInstance();
			cleanAndPutAll(contentTypeBean.resolve(protocolBean.read(location), configProfile.getFileEncoding()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (protocolBean != null) {
			protocolBean.close();
		}
	}

}
