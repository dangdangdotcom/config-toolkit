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

import com.google.common.base.Throwables;
import com.google.common.hash.Hashing;

/**
 * 
 * Do access control with ACL instead of current manual implementation.
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
public class AuthDao extends BaseDao implements IAuthDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthDao.class);

	@Override
	public boolean checkAuth(String nodeName, String password) {
		LOGGER.debug("Check auth: [{}]", nodeName);
		String hash = sha1Digest(password);
		boolean isPass = false;
		try {
			// 判断节点是否存在
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat != null) {
				byte[] data = getClient().getData().forPath(nodeName);
				isPass = hash.equals(new String(data));
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return isPass;
	}

	@Override
	public boolean auth(String nodeName, String password) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Try auth node {} with password ***", nodeName);
		}

		boolean suc = false;
		byte[] sha1Digest = sha1Digest(password).getBytes();
		try {
			// 判断节点是否存在
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat == null) {
				LOGGER.info("Node not exists, create it.");
				getClient().create().creatingParentsIfNeeded().forPath(nodeName, sha1Digest);
				suc = true;
			} else {
				LOGGER.info("Node exists.");
				byte[] data = getClient().getData().forPath(nodeName);
				// 判断节点是否被授权
				if (data == null || (data.length == 1 && data[0] != (byte) 0)) {
					getClient().setData().forPath(nodeName, sha1Digest);
					suc = true;
					LOGGER.info("Auth done.");
				} else {
					LOGGER.info("Node has been authed, cannot do duplicated authentication.");
				}
			}

		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return suc;
	}

	private String sha1Digest(String text) {
		return Hashing.sha1().hashBytes(text.getBytes()).toString();
	}
}
