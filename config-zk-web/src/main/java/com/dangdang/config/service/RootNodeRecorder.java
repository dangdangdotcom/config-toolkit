package com.dangdang.config.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Service
public class RootNodeRecorder implements IRootNodeRecorder, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<String> nodes = Sets.newHashSet();

	private static final String ROOT_NODE_CACHE_FILE = "/usr/local/tomcat/logs/root-node-cache.list";
	private static final Logger LOGGER = LoggerFactory.getLogger(RootNodeRecorder.class);

	@PostConstruct
	private void init() {
		File cacheFile = new File(ROOT_NODE_CACHE_FILE);
		if (cacheFile.exists()) {
			LOGGER.info("Loading node caches from file: {}", cacheFile.getAbsoluteFile());
			try {
				List<String> lines = FileUtils.readLines(cacheFile);
				LOGGER.info("Load cache data: {}", lines);
				nodes.addAll(lines);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dangdang.config.service.IRootNodeRecorder#saveNode(java.lang.String)
	 */
	@Override
	public void saveNode(String node) {
		nodes.add(node);
		try {
			FileUtils.writeLines(new File(ROOT_NODE_CACHE_FILE), nodes);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dangdang.config.service.IRootNodeRecorder#listNode()
	 */
	@Override
	public List<String> listNode() {
		return Lists.newArrayList(nodes);
	}

}
