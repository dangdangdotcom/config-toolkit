package com.dangdang.config.service.web.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 属性版本请求处理
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@ManagedBean(name = "versionMB")
@SessionScoped
public class VersionManagedBean implements IObserver, Serializable {

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
		refresh();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionManagedBean.class);

	private String selectedVersion;
	private List<String> versions;
	private InputText versionToBeCreatedInput;
	private InputText versionToCloneInput;

	/**
	 * 创建版本
	 */
	public void createVersion() {
		String versionToBeCreated = (String) versionToBeCreatedInput.getValue();
		if (!Strings.isNullOrEmpty(versionToBeCreated)) {

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Create version [{}].", versionToBeCreated);
			}

			boolean suc = nodeService.createProperty(ZKPaths.makePath(nodeAuth.getAuthedNode(), versionToBeCreated), null);
			if (suc) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Version created.", versionToBeCreated));

				refresh();
				selectedVersion = versionToBeCreated;
				versionToBeCreatedInput.setValue("");
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Version creation failed.", versionToBeCreated));
			}
		}
	}

	/**
	 * 克隆版本
	 */
	public void cloneVersion() {
		String versionToClone = (String) versionToCloneInput.getValue();
		if (!Strings.isNullOrEmpty(versionToClone) && !Strings.isNullOrEmpty(selectedVersion)) {

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Clone version [{}] from version [{}].", versionToClone, selectedVersion);
			}
			cloneTree(ZKPaths.makePath(nodeAuth.getAuthedNode(), selectedVersion), ZKPaths.makePath(nodeAuth.getAuthedNode(), versionToClone));
			cloneTree(ZKPaths.makePath(nodeAuth.getAuthedNode(), selectedVersion + "$"),
					ZKPaths.makePath(nodeAuth.getAuthedNode(), versionToClone + "$"));

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Version cloned.", versionToClone));
			refresh();
			selectedVersion = versionToClone;
			versionToCloneInput.setValue("");
		}
	}

	private void cloneTree(String sourceVersionPath, String destinationVersionPath) {
		List<String> sourceGroups = nodeService.listChildren(sourceVersionPath);
		if (sourceGroups != null) {
			for (String sourceGroup : sourceGroups) {
				String sourceGroupFullPath = ZKPaths.makePath(sourceVersionPath, sourceGroup);
				String destinationGroupFullPath = ZKPaths.makePath(destinationVersionPath, sourceGroup);

				nodeService.createProperty(destinationGroupFullPath, null);
				List<PropertyItem> sourceProperties = nodeService.findProperties(sourceGroupFullPath);
				if (sourceProperties != null) {
					for (PropertyItem sourceProperty : sourceProperties) {
						nodeService.createProperty(ZKPaths.makePath(destinationGroupFullPath, sourceProperty.getName()), sourceProperty.getValue());
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dangdang.config.service.observer.IObserver#notifiy(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void notified(String rootNode, String value) {
		refresh();
	}

	private void refresh() {
		String rootNode = nodeAuth.getAuthedNode();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Initialize versions for authed node: {}", rootNode);
		}

		if (!Strings.isNullOrEmpty(rootNode)) {
			List<String> children = nodeService.listChildren(rootNode);
			if (children != null) {
				children = Lists.newArrayList(Iterables.filter(children, new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return !input.endsWith("$");
					}
				}));
			}
			versions = children;
		} else {
			versions = null;
		}

		selectedVersion = null;
	}

	public final String getSelectedVersion() {
		return selectedVersion;
	}

	public final void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	public final List<String> getVersions() {
		return versions;
	}

	public final void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public InputText getVersionToBeCreatedInput() {
		return versionToBeCreatedInput;
	}

	public void setVersionToBeCreatedInput(InputText versionToBeCreatedInput) {
		this.versionToBeCreatedInput = versionToBeCreatedInput;
	}

	public InputText getVersionToCloneInput() {
		return versionToCloneInput;
	}

	public void setVersionToCloneInput(InputText versionToCloneInput) {
		this.versionToCloneInput = versionToCloneInput;
	}

}
