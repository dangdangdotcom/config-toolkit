package com.dangdang.config.service;

import java.util.List;

/**
 * 授权节点的记录，方便提示
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public interface IRootNodeRecorder {
	
	void saveNode(String node);
	
	List<String> listNode();

}
