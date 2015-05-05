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
package com.dangdang.config.service.zkdao;

import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
public class PropertyDao extends BaseDao implements IPropertyDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyDao.class);

	@Override
	public boolean createProperty(String nodeName, String value) {
		LOGGER.debug("Create property : [{}] = [{}]", nodeName, value);
		boolean suc = false;
		try {
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat == null) {
				String opResult = null;
				if (Strings.isNullOrEmpty(value)) {
					opResult = getClient().create().creatingParentsIfNeeded().forPath(nodeName);
				} else {
					opResult = getClient().create().creatingParentsIfNeeded().forPath(nodeName, value.getBytes(Charsets.UTF_8));
				}
				suc = Objects.equal(nodeName, opResult);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return suc;
	}

	@Override
	public boolean updateProperty(String nodeName, String value) {
		LOGGER.debug("Update property: [{}] = [{}]", nodeName, value);
		boolean suc = false;
		try {
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat != null) {
				Stat opResult = getClient().setData().forPath(nodeName, value.getBytes(Charsets.UTF_8));
				suc = opResult != null;
			} else {
				String opResult = getClient().create().creatingParentsIfNeeded().forPath(nodeName, value.getBytes(Charsets.UTF_8));
				suc = Objects.equal(nodeName, opResult);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return suc;
	}

	@Override
	public void deleteProperty(String nodeName) {
		LOGGER.debug("Delete property: [{}]", nodeName);
		try {
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat != null) {
				getClient().delete().deletingChildrenIfNeeded().forPath(nodeName);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

}
