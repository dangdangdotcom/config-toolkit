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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

/**
 * 属性分组请求处理
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
@ManagedBean(name = "propertyGroupMB")
@ViewScoped
public class PropertyGroupManagedBean implements Serializable, IObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyGroupManagedBean.class);

	private List<String> propertyGroups;

	public List<String> getPropertyGroups() {
		return propertyGroups;
	}

	private String selectedGroup;

	public String getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(String selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	@PostConstruct
	private void init() {
		nodeAuth.register(this);
		refreshGroups();
	}

	/**
	 * 初始化节点菜单
	 */
	public void refreshGroups() {
		notifiy(nodeAuth.getAuthedNode(), null);
	}

	/**
	 * 新分组名称
	 */
	private InputText newPropertyGroup;

	public InputText getNewPropertyGroup() {
		return newPropertyGroup;
	}

	public void setNewPropertyGroup(InputText newPropertyGroup) {
		this.newPropertyGroup = newPropertyGroup;
	}

	/**
	 * 创建新的配置组
	 */
	public void createNode() {
		String newPropertyGroupName = (String) newPropertyGroup.getValue();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Create new node: {}", newPropertyGroupName);
		}
		boolean created = nodeService.createProperty(ZKPaths.makePath(nodeAuth.getAuthedNode(), newPropertyGroupName), null);
		if (created) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property group created.", newPropertyGroupName));
			refreshGroups();
			newPropertyGroup.setValue(null);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property group creation failed.", newPropertyGroupName));
		}
	}

	/**
	 * 删除配置组
	 */
	public void deleteNode(String propertyGroup) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Delete node [{}] for property group.", propertyGroup);
		}

		nodeService.deleteProperty(ZKPaths.makePath(nodeAuth.getAuthedNode(), propertyGroup));
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property group deleted.", propertyGroup));
		refreshGroups();
	}

	/**
	 * 上传配置
	 * 
	 * @param event
	 */
	public void propertyGroupUpload(FileUploadEvent event) {
		String fileName = event.getFile().getFileName();
		LOGGER.info("Deal uploaded file: {}", fileName);
		String group = Files.getNameWithoutExtension(fileName);
		InputStream inputstream = null;
		try {
			inputstream = event.getFile().getInputstream();
			savePropertyGroup(fileName, group, inputstream);
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed", fileName + " parse error."));
			LOGGER.error("Upload File Exception.", e);
		} finally {
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					// DO NOTHING
				}
			}
		}
	}

	private void savePropertyGroup(String fileName, String group, InputStream inputstream) throws IOException {
		Reader reader = new InputStreamReader(inputstream, Charsets.UTF_8);
		Properties properties = new Properties();
		properties.load(reader);
		if (!properties.isEmpty()) {
			String groupPath = ZKPaths.makePath(nodeAuth.getAuthedNode(), group);
			boolean created = nodeService.createProperty(groupPath, null);
			if (created) {
				Map<String, String> map = Maps.fromProperties(properties);
				for (Entry<String, String> entry : map.entrySet()) {
					nodeService.createProperty(ZKPaths.makePath(groupPath, entry.getKey()), entry.getValue());
				}
				refreshGroups();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Succesful", fileName + " is uploaded."));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed", fileName + " create group " + group + " failed."));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed", fileName + " is empty."));
		}
	}

	/**
	 * 上传配置
	 * 
	 * @param event
	 */
	public void propertyZipUpload(FileUploadEvent event) {
		String fileName = event.getFile().getFileName();
		LOGGER.info("Deal uploaded file: {}", fileName);
		ZipInputStream zipInputStream = null;
		try {
			zipInputStream = new ZipInputStream(event.getFile().getInputstream());
			ZipEntry nextEntry = null;
			while ((nextEntry = zipInputStream.getNextEntry()) != null) {
				String entryName = nextEntry.getName();
				savePropertyGroup(entryName, Files.getNameWithoutExtension(entryName), zipInputStream);
			}
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed", fileName + " parse error."));
			LOGGER.error("Upload File Exception.", e);
		} finally {
			if (zipInputStream != null) {
				try {
					zipInputStream.close();
				} catch (IOException e) {
					// DO NOTHING
				}
			}
		}
	}

	@Override
	public void notifiy(String rootNode, String value) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Initialize menu for authed node: {}", rootNode);
		}

		if (!Strings.isNullOrEmpty(rootNode)) {
			propertyGroups = nodeService.listChildren(rootNode);
		}
	}
}
