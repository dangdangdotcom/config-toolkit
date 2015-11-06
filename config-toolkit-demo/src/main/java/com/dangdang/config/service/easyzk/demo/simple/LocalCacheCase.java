package com.dangdang.config.service.easyzk.demo.simple;

import com.dangdang.config.service.ConfigGroup;
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
		ZookeeperConfigProfile profile = new ZookeeperConfigProfile("zk.host", "/projectx/modulex", true);
		profile.setLocalCacheFolder("/your/local/config/folder");
		
		ConfigGroup dbConfigs = new ZookeeperConfigGroup(null, profile, "db");
	}

}
