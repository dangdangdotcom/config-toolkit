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
package com.dangdang.config.face.service;

import com.dangdang.config.face.entity.PropertyItem;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
@Service
public class NodeService implements INodeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Value("${zk}")
	private String zkAddress;

	private CuratorFramework client;

	@PostConstruct
	private void init() {
		client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	@PreDestroy
	private void destroy() {
		if (client != null) {
			client.close();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeService.class);

	@Override
	public List<PropertyItem> findProperties(String node) {
		LOGGER.debug("Find properties in node: [{}]", node);
		List<PropertyItem> properties = Lists.newArrayList();
		try {
			Stat stat = client.checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = client.getChildren();
				List<String> children = childrenBuilder.forPath(node);
				GetDataBuilder dataBuilder = client.getData();
				if (children != null) {
					for (String child : children) {
						String propPath = ZKPaths.makePath(node, child);
						PropertyItem item = new PropertyItem(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
						properties.add(item);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return properties;
	}

	@Override
	public List<String> listChildren(String node) {
		LOGGER.debug("Find children of node: [{}]", node);
		List<String> children = null;
		try {
			Stat stat = client.checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = client.getChildren();
				children = childrenBuilder.forPath(node);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return children;
	}

	@Override
	public boolean createProperty(String node) {
		return createProperty(node, null);
	}

	@Override
	public boolean createProperty(String node, String value) {
		LOGGER.debug("Create property : [{}] = [{}]", node, value);
		boolean suc = false;
		try {
			Stat stat = client.checkExists().forPath(node);
			if (stat == null) {
				final byte[] data = Strings.isNullOrEmpty(value) ? new byte[]{} : value.getBytes(Charsets.UTF_8);
				String opResult = client.create().creatingParentsIfNeeded().forPath(node, data);
				suc = Objects.equal(node, opResult);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return suc;
	}

	@Override
	public boolean updateProperty(String node, String value) {
		LOGGER.debug("Update property: [{}] = [{}]", node, value);
		boolean suc = false;
		try {
			Stat stat = client.checkExists().forPath(node);
			if (stat != null) {
				Stat opResult = client.setData().forPath(node, value.getBytes(Charsets.UTF_8));
				suc = opResult != null;
			} else {
				String opResult = client.create().creatingParentsIfNeeded().forPath(node, value.getBytes(Charsets.UTF_8));
				suc = Objects.equal(node, opResult);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return suc;
	}

	@Override
	public void deleteProperty(String node) {
		LOGGER.debug("Delete property: [{}]", node);
		try {
			Stat stat = client.checkExists().forPath(node);
			if (stat != null) {
				client.delete().deletingChildrenIfNeeded().forPath(node);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public String getValue(String node) {
		try {
			// 判断节点是否存在
			Stat stat = client.checkExists().forPath(node);
			if (stat != null) {
				byte[] data = client.getData().forPath(node);
				return new String(data);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
