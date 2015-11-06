package com.dangdang.config.service.easyzk.demo.simple;

import com.dangdang.config.service.zookeeper.ConfigLocalCache;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class LocalCacheCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String rootNode = "/projectx/modulex";
		ZookeeperConfigProfile profile = new ZookeeperConfigProfile("zk.host", rootNode, true);

		ZookeeperConfigGroup dbConfigs = new ZookeeperConfigGroup(null, profile, "db");
		dbConfigs.setConfigLocalCache(new ConfigLocalCache("/your/local/config/folder", rootNode));
	}

}
