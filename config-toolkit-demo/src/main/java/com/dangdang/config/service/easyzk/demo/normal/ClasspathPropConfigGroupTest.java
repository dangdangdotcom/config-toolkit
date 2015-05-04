package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileConfigProfile;

public class ClasspathPropConfigGroupTest {
	
	public static void main(String[] args) {
		FileConfigProfile configProfile = new FileConfigProfile("UTF8", "properties");
		ConfigGroup configGroup = new FileConfigGroup(configProfile, "file:/Users/yuxuanwang/Work/git/config-toolkit/config-toolkit-demo/src/main/resources/property-group1.properties");
		
		while(true) {
			System.out.println(configGroup.get("int_property_key"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
