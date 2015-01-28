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
package com.dangdang.config.service.easyzk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.observer.AbstractSubject;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 节点
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ConfigNode extends AbstractSubject {

	private ConfigProfile configProfile;

	/**
	 * 节点名字
	 */
	String node;

	private KeyLoadingMode keyLoadingMode;

	/**
	 * 需要包含或排除的key,由{@code KeyLoadingMode}决定
	 */
	private Set<String> keysSpecified;

	private CuratorFramework client;

	private ConfigLocalCache configLocalCache;

	public void setConfigLocalCache(ConfigLocalCache configLocalCache) {
		this.configLocalCache = configLocalCache;
	}

	/**
	 * 节点的下属性映射
	 */
	private final Map<String, String> properties = Maps.newConcurrentMap();

	static final Logger LOGGER = LoggerFactory.getLogger(ConfigNode.class);

	protected ConfigNode(ConfigProfile configProfile, String node) {
		super();
		this.configProfile = configProfile;
		this.node = node;
	}

	public void defineKeyLoadingPattern(KeyLoadingMode keyLoadingMode, Set<String> keysSpecified) {
		this.keyLoadingMode = Preconditions.checkNotNull(keyLoadingMode);
		this.keysSpecified = keysSpecified != null ? Sets.newHashSet(keysSpecified) : keysSpecified;
	}

	/**
	 * 初始化节点
	 */
	protected void initConfigNode() {
		client = CuratorFrameworkFactory.newClient(configProfile.getConnectStr(), configProfile.getRetryPolicy());
		client.getCuratorListenable().addListener(new ConfigNodeEventListener(this));
		client.start();
		loadNode(false);

		// Update local cache
		if (configLocalCache != null) {
			configLocalCache.saveLocalCache(this, node);
		}

		// Consistency check
		if (configProfile.isConsistencyCheck()) {
			new Timer().scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					LOGGER.info("Do consistency check for node: {}", node);
					loadNode(true);
				}
			}, 0L, configProfile.getConsistencyCheckRate());
		}
	}

	/**
	 * 加载节点并监听节点变化
	 */
	void loadNode(boolean consistencyCheck) {
		final String nodePath = ZKPaths.makePath(configProfile.getRootNode(), node);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Loading properties for node: [%s], with loading mode: [%s] and keys specified: [%s]", nodePath,
					keyLoadingMode, keysSpecified));
		}

		GetChildrenBuilder childrenBuilder = client.getChildren();

		try {
			if (!consistencyCheck) {
				properties.clear();
			}
			List<String> children = childrenBuilder.watched().forPath(nodePath);
			if (children != null) {
				for (String child : children) {
					loadKey(ZKPaths.makePath(nodePath, child));
				}
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	/**
	 * 加载属性并监听属性变化
	 * 
	 * @param nodePath
	 *            属性的路径
	 * @throws Exception
	 */
	void loadKey(final String nodePath) throws Exception {
		String nodeName = ZKPaths.getNodeFromPath(nodePath);
		switch (keyLoadingMode) {
		case INCLUDE:
			if (!keysSpecified.contains(nodeName)) {
				return;
			}
			break;
		case EXCLUDE:
			if (keysSpecified.contains(nodeName)) {
				return;
			}
			break;
		case NONE:
			break;
		default:
			break;
		}

		GetDataBuilder data = client.getData();
		String childValue = new String(data.watched().forPath(nodePath), Charsets.UTF_8);

		if (Objects.equal(childValue, properties.get(nodeName))) {
			LOGGER.debug("Key data not change, ignore: key[{}]", nodeName);
		} else {
			LOGGER.debug("Loading data: key[{}] - value[{}]", nodeName, childValue);
			properties.put(nodeName, childValue);

			// 通知注册者
			notify(nodeName, childValue);
		}
	}

	/**
	 * 关闭连接
	 */
	@PreDestroy
	private void destroy() {
		if (client != null) {
			client.close();
		}
	}

	/**
	 * 获取属性值
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}

	public String getNode() {
		return node;
	}

	public ConfigLocalCache getConfigLocalCache() {
		return configLocalCache;
	}

	/**
	 * 导出属性列表
	 * 
	 * @return
	 */
	public Map<String, String> exportProperties() {
		return Maps.newHashMap(properties);
	}

	/**
	 * 导入属性列表
	 * 
	 * @param imports
	 */
	public void importProperties(Map<String, String> imports) {
		this.properties.putAll(imports);
	}

	/**
	 * 节点下属性的加载模式
	 * 
	 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
	 *
	 */
	public static enum KeyLoadingMode {
		/**
		 * 加载所有属性
		 */
		NONE,
		/**
		 * 包含某些属性
		 */
		INCLUDE,
		/**
		 * 排除某些属性
		 */
		EXCLUDE;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("configProfile", configProfile).add("node", node).add("keyLoadingMode", keyLoadingMode)
				.add("keysSpecified", keysSpecified).add("properties", properties).toString();
	}

}
