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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * 监听器
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public final class ConfigNodeEventListener implements CuratorListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigNodeEventListener.class);
	
	private final ConfigNode configNode;

	public ConfigNodeEventListener(ConfigNode configNode) {
		super();
		this.configNode = Preconditions.checkNotNull(configNode);
	}

	@Override
	public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(event.toString());
		}

		final WatchedEvent watchedEvent = event.getWatchedEvent();
		if (watchedEvent != null) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(watchedEvent.toString());
			}

			if (watchedEvent.getState() == KeeperState.SyncConnected) {
				boolean someChange = false;
				switch (watchedEvent.getType()) {
				case NodeChildrenChanged:
					configNode.loadNode(false);
					someChange = true;
					break;
				case NodeDataChanged:
					configNode.loadKey(watchedEvent.getPath());
					someChange = true;
					break;
				default:
					break;
				}

				if (someChange && configNode.getConfigLocalCache() != null) {
					configNode.getConfigLocalCache().saveLocalCache(configNode, configNode.getNode());
				}
			}
		}
	}
}
