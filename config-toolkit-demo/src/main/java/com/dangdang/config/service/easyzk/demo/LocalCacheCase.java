package com.dangdang.config.service.easyzk.demo;

import com.dangdang.config.service.easyzk.ConfigFactory;
import com.dangdang.config.service.easyzk.ConfigNode;
import com.dangdang.config.service.easyzk.ConfigProfile;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class LocalCacheCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigProfile profile = new ConfigProfile("zk.host", "/projectx/modulex", true);
		profile.setLocalCacheFolder("/your/local/config/folder");
		
		ConfigFactory configFactory = new ConfigFactory(profile);
		ConfigNode dbConfigs = configFactory.getConfigNode("db");
	}

}
