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
package com.dangdang.config.service.web.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.curator.utils.ZKPaths;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItem;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * Properties export
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@ManagedBean(name = "propertyExportMB")
@RequestScoped
public class PropertyExportManagedBean {

	@ManagedProperty(value = "#{nodeService}")
	private INodeService nodeService;

	public void setNodeService(INodeService nodeService) {
		this.nodeService = nodeService;
	}

	@ManagedProperty(value = "#{nodeAuthMB}")
	private NodeAuthManagedBean nodeAuth;

	public void setNodeAuth(NodeAuthManagedBean nodeAuth) {
		this.nodeAuth = nodeAuth;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyExportManagedBean.class);

	public StreamedContent generateFile(String groupName) {
		LOGGER.info("Export config group: {}", groupName);

		StreamedContent file = null;
		if (!Strings.isNullOrEmpty(groupName)) {
			String groupPath = ZKPaths.makePath(nodeAuth.getAuthedNode(), groupName);
			Properties properties = childrenToProperties(groupPath);
			if (!properties.isEmpty()) {
				ByteArrayOutputStream out = null;
				try {
					out = new ByteArrayOutputStream();
					properties.store(out, String.format("Export from zookeeper configuration group: [%s].", groupName));
					InputStream in = new ByteArrayInputStream(out.toByteArray());

					String fileName = ZKPaths.getNodeFromPath(groupPath) + ".properties";
					file = new DefaultStreamedContent(in, "text/plain", fileName, Charsets.UTF_8.name());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// DO NOTHING
						}
					}
				}
			}
		}
		return file;
	}

	private Properties childrenToProperties(String path) {
		Properties properties = new Properties();
		List<PropertyItem> propertyItems = nodeService.findProperties(path);
		if (propertyItems != null && !propertyItems.isEmpty()) {
			for (PropertyItem propertyItem : propertyItems) {
				properties.put(propertyItem.getName(), propertyItem.getValue());
			}
		}
		return properties;
	}

	public StreamedContent generateFileAll() {
		LOGGER.info("Export all config group");
		StreamedContent file = null;

		String authedNode = nodeAuth.getAuthedNode();
		List<String> children = nodeService.listChildren(authedNode);
		if (children != null && !children.isEmpty()) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(out);
				for (String child : children) {
					String groupPath = ZKPaths.makePath(authedNode, child);
					String fileName = ZKPaths.getNodeFromPath(groupPath) + ".properties";

					Properties properties = childrenToProperties(groupPath);
					if (!properties.isEmpty()) {
						ZipEntry zipEntry = new ZipEntry(fileName);
						zipOutputStream.putNextEntry(zipEntry);
						properties.store(zipOutputStream, String.format("Export from zookeeper configuration group: [%s].", groupPath));
						zipOutputStream.closeEntry();
					}
				}

				zipOutputStream.close();
				byte[] data = out.toByteArray();
				InputStream in = new ByteArrayInputStream(data);

				String fileName = authedNode.replace('/', '-') + ".zip";
				file = new DefaultStreamedContent(in, "application/zip", fileName, Charsets.UTF_8.name());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		return file;
	}
}
