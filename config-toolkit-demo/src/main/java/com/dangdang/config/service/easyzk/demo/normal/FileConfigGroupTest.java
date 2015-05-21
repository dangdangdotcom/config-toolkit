package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileConfigProfile;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

public class FileConfigGroupTest {
	
	public static void main(String[] args) {
		ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("zoo.host1:8181", "/projectx/modulex", "1.0.0");
		ConfigGroup zkConfigGroup = new ZookeeperConfigGroup(configProfile , "property-group1");
		FileConfigProfile fileConfigProfile = new FileConfigProfile("UTF8", "properties");
		ConfigGroup configGroup = new FileConfigGroup(zkConfigGroup, fileConfigProfile, "classpath:property-group1.properties");
//		ConfigGroup configGroup = new FileConfigGroup(configProfile, "file:/Users/yuxuanwang/Work/git/config-toolkit/config-toolkit-demo/src/main/resources/property-group1.properties");
		
		while(true) {
			Object obj  = "int_property_key";
			System.out.println(configGroup.get(obj));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
