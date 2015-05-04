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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.GeneralConfigGroup;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

/**
 * 配置组节点
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ZookeeperConfigGroup extends GeneralConfigGroup {

	private static final long serialVersionUID = 1L;

	private ZookeeperConfigProfile configProfile;

	/**
	 * 节点名字
	 */
	private String node;

	private CuratorFramework client;

	private ConfigLocalCache configLocalCache;

	public void setConfigLocalCache(ConfigLocalCache configLocalCache) {
		this.configLocalCache = configLocalCache;
	}

	static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperConfigGroup.class);

	public ZookeeperConfigGroup(ZookeeperConfigProfile configProfile, String node) {
		this(null, configProfile, node);
	}

	public ZookeeperConfigGroup(ConfigGroup internalConfigGroup, ZookeeperConfigProfile configProfile, String node) {
		super(internalConfigGroup);
		this.configProfile = configProfile;
		this.node = node;
		initConfigs();
	}

	private Timer timer;

	private CuratorListener listener = new ConfigNodeEventListener(this);

	/**
	 * 初始化节点
	 */
	private void initConfigs() {
		client = CuratorFrameworkFactory.newClient(configProfile.getConnectStr(), configProfile.getRetryPolicy());
		client.start();
		client.getCuratorListenable().addListener(listener);

		LOGGER.debug("Loading properties for node: {}, with loading mode: {} and keys specified: {}", node, configProfile.getKeyLoadingMode(),
				configProfile.getKeysSpecified());
		loadNode();

		// Update local cache
		if (configLocalCache != null) {
			configLocalCache.saveLocalCache(this, node);
		}

		// Consistency check
		if (configProfile.isConsistencyCheck()) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					LOGGER.trace("Do consistency check for node: {}", node);
					loadNode();
				}
			}, 60000L, configProfile.getConsistencyCheckRate());
		}
	}

	/**
	 * 加载节点并监听节点变化
	 */
	void loadNode() {
		final String nodePath = ZKPaths.makePath(configProfile.getVersionedRootNode(), node);

		GetChildrenBuilder childrenBuilder = client.getChildren();

		try {
			List<String> children = childrenBuilder.watched().forPath(nodePath);
			if (children != null) {
				Map<String, String> configs = Maps.newHashMap();
				for (String child : children) {
					Pair<String, String> keyValue = loadKey(ZKPaths.makePath(nodePath, child));
					if (keyValue != null) {
						configs.put(keyValue.getKey(), keyValue.getValue());
					}
				}
				super.putAll(configs);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
	
	void reloadKey(final String nodePath) {
		try {
			Pair<String, String> keyValue = loadKey(nodePath);
			if(keyValue != null) {
				super.put(keyValue.getKey(), keyValue.getValue());
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private Pair<String, String> loadKey(final String nodePath) throws Exception {
		String nodeName = ZKPaths.getNodeFromPath(nodePath);
		Set<String> keysSpecified = configProfile.getKeysSpecified();
		switch (configProfile.getKeyLoadingMode()) {
		case INCLUDE:
			if (keysSpecified == null || !keysSpecified.contains(nodeName)) {
				return null;
			}
			break;
		case EXCLUDE:
			if (keysSpecified.contains(nodeName)) {
				return null;
			}
			break;
		case ALL:
			break;
		default:
			break;
		}

		GetDataBuilder data = client.getData();
		String value = new String(data.watched().forPath(nodePath), Charsets.UTF_8);
		return new ImmutablePair<String, String>(nodeName, value);
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
		return Maps.newHashMap(this);
	}

	@PreDestroy
	@Override
	public void close() {
		if (timer != null) {
			timer.cancel();
		}
		if (client != null) {
			client.getCuratorListenable().removeListener(listener);
			client.close();
		}

	}

}
