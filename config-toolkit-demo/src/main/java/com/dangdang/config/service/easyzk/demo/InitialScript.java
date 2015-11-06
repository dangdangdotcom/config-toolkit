package com.dangdang.config.service.easyzk.demo;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import com.google.common.collect.Maps;

/**
 * 先运行我
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public class InitialScript {

	/**
	 * Change This To Your Zk Address
	 */
	private static final String ZK = "config-toolkit.mabaoshan.com:8011";

	private static final Map<String, String> data = Maps.newHashMap();

	static {
		data.put("/projectx/modulex/1.0.0/property-group1/string_property_key", "Config-Toolkit");
		data.put("/projectx/modulex/1.0.0/property-group1/int_property_key", "1123");
		data.put("/projectx/modulex/1.0.0/property-group1/cool", "true");
		data.put("/projectx/modulex/1.0.0/property-group2/cool", "false");
	}

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(ZK, new ExponentialBackoffRetry(100, 2));
		client.start();

		for (Entry<String, String> item : data.entrySet()) {
			Stat stat = client.checkExists().forPath(item.getKey());
			if (stat == null) {
				client.create().creatingParentsIfNeeded().forPath(item.getKey(), item.getValue().getBytes());
			}
		}
	}

}
