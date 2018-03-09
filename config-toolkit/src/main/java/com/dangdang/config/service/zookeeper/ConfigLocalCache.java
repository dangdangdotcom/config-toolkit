/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.service.zookeeper;

import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 配置本地缓存
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ConfigLocalCache {

	private String localCachePath;

	public ConfigLocalCache(String localCacheFolder, String rootNode) {
		super();
		this.localCachePath = ZKPaths.makePath(localCacheFolder, rootNode);
	}

	private static final String SUFFIX = ".cache";

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLocalCache.class);

	/**
	 * 缓存配置到本地
	 * 
	 * @param configNode
	 * @param node
	 */
	public void saveLocalCache(ZookeeperConfigGroup configNode, String node) {
		String localFilePath = genCacheFilePath(node);
		LOGGER.debug("Saving cache to file: {}", localFilePath);

		Map<String, String> data = configNode.exportProperties();
		if (data != null && data.size() > 0) {
			Properties properties = new Properties();
			for (Entry<String, String> entry : data.entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
			}
			Writer writer = null;
			try {
				writer = new OutputStreamWriter(new FileOutputStream(localFilePath), "UTF-8");
				properties.store(writer, String.format("Local cache of configs group: %s", node));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// DO NOTHING
					}
				}
			}
		}
	}

	/**
	 * 计算本地缓存文件位置
	 * 
	 * @param node
	 * @return
	 */
	private String genCacheFilePath(String node) {
		checkFolderExistence();
		StringBuilder builder = new StringBuilder();
		builder.append(localCachePath);
		builder.append(File.separatorChar);
		builder.append(node);
		builder.append(SUFFIX);

		return builder.toString();
	}

	/**
	 * 检查本地缓存文件的存在状态，如不存在，创建
	 */
	private void checkFolderExistence() {
		File file = new File(localCachePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	@Override
	public String toString() {
		return "ConfigLocalCache{" +
				"localCachePath='" + localCachePath + '\'' +
				'}';
	}
}
