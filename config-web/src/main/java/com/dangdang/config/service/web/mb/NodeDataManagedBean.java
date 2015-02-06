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

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Strings;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 * 
 */
@ManagedBean(name = "nodeDataMB")
@ViewScoped
public class NodeDataManagedBean implements Serializable, IObserver {

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

	@PostConstruct
	private void init() {
		nodeAuth.register(this);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeDataManagedBean.class);

	private String selectedNode;

	public String getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(String selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * 刷新节点属性
	 */
	public void refreshNodeProperties(String selectedNode) {
		this.selectedNode = selectedNode;
		String nodePath = getSelectedNodePath();

		LOGGER.info("Find properties of node: [{}].", nodePath);

		if (Strings.isNullOrEmpty(nodePath)) {
			nodeProps = null;
		} else {
			nodeProps = nodeService.findProperties(nodePath);
		}
	}

	/**
	 * 获取已选节点全路径
	 * 
	 * @return
	 */
	private String getSelectedNodePath() {
		if (Strings.isNullOrEmpty(selectedNode))
			return null;
		return ZKPaths.makePath(nodeAuth.getAuthedNode(), selectedNode);
	}

	/**
	 * 获取属性全路径
	 * 
	 * @param propertyName
	 * @return
	 */
	private String getPropertyNodePath(String propertyName) {
		return ZKPaths.makePath(getSelectedNodePath(), propertyName);
	}

	/**
	 * 节点下的属性列表
	 * 
	 */
	private List<PropertyItem> nodeProps;

	public void setNodeProps(List<PropertyItem> nodeProps) {
		this.nodeProps = nodeProps;
	}

	public List<PropertyItem> getNodeProps() {
		return nodeProps;
	}

	/**
	 * 更新数据
	 * 
	 * @param event
	 */
	public void onPropertyEdit(RowEditEvent event) {
		if (!checkPropertyGroupCheckedStatus()) {
			return;
		}

		PropertyItem selectedItem = (PropertyItem) event.getObject();

		LOGGER.info("Update property with : {}.", selectedItem);

		String name = selectedItem.getName();
		String oriName = selectedItem.getOriName();

		boolean suc = false;
		if (name.equals(oriName)) {
			suc = nodeService.updateProperty(getPropertyNodePath(name), selectedItem.getValue());
		} else {
			nodeService.deleteProperty(getPropertyNodePath(oriName));
			suc = nodeService.createProperty(getPropertyNodePath(name), selectedItem.getValue());
		}

		if (suc) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property Saved suc.", name));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Property Saved failed.", name));
		}

	}

	/**
	 * 新属性名字
	 */
	private InputText newPropName;
	/**
	 * 新属性值
	 */
	private InputText newPropValue;

	public InputText getNewPropName() {
		return newPropName;
	}

	public void setNewPropName(InputText newPropName) {
		this.newPropName = newPropName;
	}

	public InputText getNewPropValue() {
		return newPropValue;
	}

	public void setNewPropValue(InputText newPropValue) {
		this.newPropValue = newPropValue;
	}

	/**
	 * 创建新属性
	 */
	public void createProperty() {
		if (!checkPropertyGroupCheckedStatus()) {
			return;
		}

		String propName = (String) newPropName.getValue();
		String propValue = (String) newPropValue.getValue();

		String propPath = getPropertyNodePath(propName);
		LOGGER.info("Create property: Path[{}], Value[{}]", propPath, propValue);
		boolean created = nodeService.createProperty(propPath, propValue);
		if (created) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property created.", propPath));
			refreshNodeProperties(selectedNode);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Property creation failed.", propPath));
		}
	}

	/**
	 * 删除原有属性
	 * 
	 * @param propName
	 */
	public void deleteProperty(String propName) {
		if (!checkPropertyGroupCheckedStatus()) {
			return;
		}

		String propPath = getPropertyNodePath(propName);
		LOGGER.info("Delete property: Path[{}]", propPath);

		nodeService.deleteProperty(propPath);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property deleted.", propPath));
		refreshNodeProperties(selectedNode);
	}

	/**
	 * 检查属性组是否选中
	 * 
	 * @return true: 已选中
	 */
	private boolean checkPropertyGroupCheckedStatus() {
		boolean notChecked = Strings.isNullOrEmpty(selectedNode);
		if (notChecked) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select a property group first."));
		}
		return !notChecked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dangdang.config.service.observer.IObserver#notifiy(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void notifiy(String data, String value) {
		nodeProps = null;
	}
}
