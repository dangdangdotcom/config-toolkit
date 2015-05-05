package com.dangdang.config.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.entity.PropertyItemVO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Service
public class NodeBusiness implements INodeBusiness {

	@Autowired
	private INodeService nodeService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dangdang.config.business.INodeBusiness#findPropertyItems(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<PropertyItemVO> findPropertyItems(String rootNode, String version, String group) {
		List<PropertyItemVO> items = null;
		if (!Strings.isNullOrEmpty(rootNode) && !Strings.isNullOrEmpty(version) && !Strings.isNullOrEmpty(group)) {
			List<PropertyItem> propertyItems = nodeService.findProperties(getGroupFullPath(rootNode, version, group));
			List<PropertyItem> propertyComments = nodeService.findProperties(getGroupCommentFullPath(rootNode, version, group));
			if (propertyItems != null) {
				Map<String, String> comments = Maps.newHashMap();
				if (propertyComments != null) {
					for (PropertyItem comment : propertyComments) {
						comments.put(comment.getName(), comment.getValue());
					}
				}

				items = Lists.newArrayList();
				for (PropertyItem propertyItem : propertyItems) {
					PropertyItemVO vo = new PropertyItemVO(propertyItem);
					vo.setComment(comments.get(propertyItem.getName()));
					items.add(vo);
				}
				
				Collections.sort(items);
			}
		}
		return items;
	}

	private String getGroupFullPath(String rootNode, String version, String group) {
		String authedNode = ZKPaths.makePath(rootNode, version);
		return ZKPaths.makePath(authedNode, group);
	}

	private String getGroupCommentFullPath(String rootNode, String version, String group) {
		String authedNode = ZKPaths.makePath(rootNode, version + "$");
		return ZKPaths.makePath(authedNode, group);
	}

}
