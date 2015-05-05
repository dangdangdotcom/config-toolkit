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
package com.dangdang.config.service.support.spring;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.springframework.core.env.PropertySource;

import com.dangdang.config.service.ConfigGroup;

/**
 * Spring Property Sources support
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ConfigGroupResource extends PropertySource<ConfigGroup> implements Closeable {

	public ConfigGroupResource(ConfigGroup configNode) {
		super(UUID.randomUUID().toString(), configNode);
	}

	@Override
	public Object getProperty(String name) {
		return super.getSource().get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	@PreDestroy
	public void close() throws IOException {
		super.getSource().close();
	}

}
