package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.classpathfile.ClasspathFileConfigProfile;
import com.dangdang.config.service.classpathfile.ClasspathPropConfigGroup;

public class ClasspathPropConfigGroupTest {
	
	public static void main(String[] args) {
		ClasspathFileConfigProfile configProfile = new ClasspathFileConfigProfile();
		ConfigGroup configGroup = new ClasspathPropConfigGroup(configProfile, "property-group1.properties");
		
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
