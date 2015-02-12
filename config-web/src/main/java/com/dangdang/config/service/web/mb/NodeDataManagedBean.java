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

import com.dangdang.config.business.INodeBusiness;
import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItemVO;
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

	@ManagedProperty(value = "#{nodeBusiness}")
	private INodeBusiness nodeBusiness;

	public void setNodeBusiness(INodeBusiness nodeBusiness) {
		this.nodeBusiness = nodeBusiness;
	}

	@ManagedProperty(value = "#{nodeAuthMB}")
	private NodeAuthManagedBean nodeAuth;

	public void setNodeAuth(NodeAuthManagedBean nodeAuth) {
		this.nodeAuth = nodeAuth;
	}

	@ManagedProperty(value = "#{versionMB}")
	private VersionManagedBean versionMB;

	public void setVersionMB(VersionManagedBean versionMB) {
		this.versionMB = versionMB;
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
		LOGGER.info("Find properties of node: [{}].", selectedNode);

		nodeProps = nodeBusiness.findPropertyItems(nodeAuth.getAuthedNode(), versionMB.getSelectedVersion(), selectedNode);
	}

	/**
	 * 获取已选节点全路径
	 * 
	 * @return
	 */
	private String getSelectedNodePath() {
		if (Strings.isNullOrEmpty(selectedNode))
			return null;
		String authedNode = ZKPaths.makePath(nodeAuth.getAuthedNode(), versionMB.getSelectedVersion());
		return ZKPaths.makePath(authedNode, selectedNode);
	}

	/**
	 * 获取已选节点注释全路径
	 * 
	 * @return
	 */
	private String getSelectedNodeCommentPath() {
		if (Strings.isNullOrEmpty(selectedNode))
			return null;
		String authedNode = ZKPaths.makePath(nodeAuth.getAuthedNode(), versionMB.getSelectedVersion() + "$");
		return ZKPaths.makePath(authedNode, selectedNode);
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
	 * 获取属性注释全路径
	 * 
	 * @param propertyName
	 * @return
	 */
	private String getPropertyCommentPath(String propertyName) {
		return ZKPaths.makePath(getSelectedNodeCommentPath(), propertyName);
	}

	/**
	 * 节点下的属性列表
	 * 
	 */
	private List<PropertyItemVO> nodeProps;

	public void setNodeProps(List<PropertyItemVO> nodeProps) {
		this.nodeProps = nodeProps;
	}

	public List<PropertyItemVO> getNodeProps() {
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

		PropertyItemVO selectedItem = (PropertyItemVO) event.getObject();

		LOGGER.info("Update property with : {}.", selectedItem);

		String name = selectedItem.getName();
		String oriName = selectedItem.getOriName();

		boolean suc = false;
		String fullPropPath = getPropertyNodePath(name);
		String fullCommentPath = getPropertyCommentPath(name);
		if (name.equals(oriName)) {
			suc = nodeService.updateProperty(fullPropPath, selectedItem.getValue());
			nodeService.updateProperty(fullCommentPath, selectedItem.getComment());
		} else {
			nodeService.deleteProperty(getPropertyNodePath(oriName));
			suc = nodeService.createProperty(fullPropPath, selectedItem.getValue());

			nodeService.deleteProperty(fullCommentPath);
			nodeService.createProperty(fullCommentPath, selectedItem.getComment());
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

	/**
	 * 注释值
	 */
	private InputText newCommentValue;

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

	public InputText getNewCommentValue() {
		return newCommentValue;
	}

	public void setNewCommentValue(InputText newCommentValue) {
		this.newCommentValue = newCommentValue;
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
		String propComment = (String) newCommentValue.getValue();

		String propPath = getPropertyNodePath(propName);
		LOGGER.info("Create property: Path[{}], Value[{}]", propPath, propValue);
		boolean created = nodeService.createProperty(propPath, propValue);
		if (created) {
			if (!Strings.isNullOrEmpty(propComment)) {
				nodeService.createProperty(getPropertyCommentPath(propName), propComment);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property created.", propPath));
			refreshNodeProperties(selectedNode);

			newPropName.setValue(null);
			newPropValue.setValue(null);
			newCommentValue.setValue(null);
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
		nodeService.deleteProperty(getPropertyCommentPath(propName));

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
	public void notified(String data, String value) {
		nodeProps = null;
	}
}
