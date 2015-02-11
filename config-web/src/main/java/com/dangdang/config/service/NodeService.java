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
package com.dangdang.config.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.zkdao.INodeDao;
import com.dangdang.config.service.zkdao.IPropertyDao;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Service
public class NodeService implements INodeService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private INodeDao nodeDao;

	@Autowired
	private IPropertyDao propertyDao;

	@Override
	public List<PropertyItem> findProperties(String node) {
		return nodeDao.findProperties(node);
	}

	@Override
	public List<String> listChildren(String node) {
		List<String> children = nodeDao.listChildren(node);
		if (children != null) {
			Collections.sort(children);
		}
		return children;
	}

	@Override
	public boolean createProperty(String nodeName, String value) {
		return propertyDao.createProperty(nodeName, value);
	}

	@Override
	public boolean updateProperty(String nodeName, String value) {
		return propertyDao.updateProperty(nodeName, value);
	}

	@Override
	public void deleteProperty(String nodeName) {
		propertyDao.deleteProperty(nodeName);
	}

}
