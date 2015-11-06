/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.service.easyzk.demo.override;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileConfigProfile;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;
import com.google.common.base.Preconditions;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class LocalOverrideTest {

	public static void main(String[] args) {
		ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("config-toolkit.mabaoshan.com:8011", "/projectx/modulex", "1.0.0");
		ZookeeperConfigGroup propertyGroup1 = new ZookeeperConfigGroup(configProfile, "property-group1");
		
		ConfigGroup group = new FileConfigGroup(propertyGroup1, new FileConfigProfile("UTF-8", "properties"), "classpath:property-group1.properties");

		System.out.println(group);
		
		String stringProperty = group.get("string_property_key");
		System.out.println(stringProperty);
		Preconditions.checkState("Config-Toolkit".equals(stringProperty));
		
		String intProperty = group.get("int_property_key");
		System.out.println(intProperty);
		Preconditions.checkState(123456789 == Integer.parseInt(intProperty));
	}

}
