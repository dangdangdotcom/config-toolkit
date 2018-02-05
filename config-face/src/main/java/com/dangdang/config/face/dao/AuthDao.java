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
package com.dangdang.config.face.dao;

import com.google.common.base.Throwables;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 
 * Do access control with ACL instead of current manual implementation.
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
@Repository
public class AuthDao extends BaseDao implements IAuthDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthDao.class);

	@Override
	public String getAuth(String nodeName) {
		LOGGER.debug("Check auth: [{}]", nodeName);
		try {
			// 判断节点是否存在
			Stat stat = getClient().checkExists().forPath(nodeName);
			if (stat != null) {
				byte[] data = getClient().getData().forPath(nodeName);
				return new String(data);
			}
		} catch (Exception e) {
			Throwables.throwIfUnchecked(e);
		}
		return null;
	}

}
