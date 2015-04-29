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
package com.dangdang.config.service.easyzk.support.spring;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;

import com.dangdang.config.service.easyzk.ConfigFactory;
import com.dangdang.config.service.easyzk.ConfigNode;
import com.google.common.base.Preconditions;

/**
 * Factory to create PropertySource for zookeeper
 * 
 * @author Wang Yuxuan
 *
 */
public class ZookeeperSourceFactory {

	public static PropertySources create(ConfigNode... configNodes) {
		Preconditions.checkNotNull(configNodes);
		final MutablePropertySources sources = new MutablePropertySources();
		for (ConfigNode configNode : configNodes) {
			sources.addLast(new ZookeeperResource(configNode));
		}
		return sources;
	}

	public static PropertySources create(ConfigFactory configFactory, String... nodes) {
		Preconditions.checkNotNull(configFactory);
		Preconditions.checkNotNull(nodes);
		ConfigNode[] configNodes = new ConfigNode[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			configNodes[i] = configFactory.getConfigNode(nodes[i]);
		}

		return create(configNodes);
	}

}
