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
package com.dangdang.config.service.easyzk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * 基本配置
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ConfigProfile {

	/**
	 * zookeeper地址
	 */
	private final String connectStr;

	/**
	 * 项目配置根目录
	 */
	private final String rootNode;

	/**
	 * 重试策略
	 */
	private final RetryPolicy retryPolicy;

	public ConfigProfile(final String connectStr, final String rootNode) {
		this(connectStr, rootNode, new ExponentialBackoffRetry(100, 2));
	}

	public ConfigProfile(final String connectStr, final String rootNode, final RetryPolicy retryPolicy) {
		super();
		this.connectStr = Preconditions.checkNotNull(connectStr);
		this.rootNode = Preconditions.checkNotNull(rootNode);
		this.retryPolicy = Preconditions.checkNotNull(retryPolicy);
	}

	public String getConnectStr() {
		return connectStr;
	}

	public String getRootNode() {
		return rootNode;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("connectStr", connectStr).add("rootNode", rootNode).add("retryPolicy", retryPolicy).toString();
	}
}
