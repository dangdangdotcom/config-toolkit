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
package com.dangdang.config.service.easyzk.demo.spring.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.config.service.ConfigGroup;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileConfigProfile;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

/**
 * Example with spring annotation
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
@Configuration
public class ConfigCenter {

	@Bean
	public ZookeeperConfigProfile getConfigProfile() {
		return new ZookeeperConfigProfile("zoo.host1:8181,zoo.host2:8181,zoo.host3:8181", "/projectx/modulex", "1.0.0");
	}

	@Bean(name = "propertyGroup1")
	public ConfigGroup getPropertyGroup1(ZookeeperConfigProfile zookeeperConfigProfile) {
		ZookeeperConfigGroup zkGroup = new ZookeeperConfigGroup(zookeeperConfigProfile, "property-group1");
		return new FileConfigGroup(zkGroup, new FileConfigProfile("UTF8", "properties"), "classpath:property-group1.properties");
	}
}
