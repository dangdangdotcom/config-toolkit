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
package com.dangdang.config.service.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

import com.dangdang.config.service.ConfigProfile;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 基本配置
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class ZookeeperConfigProfile extends ConfigProfile {

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

	/**
	 * 是否打开本地文件缓存
	 */
	private final boolean openLocalCache;

	/**
	 * 本地缓存文件目录
	 */
	private String localCacheFolder = "/config-service";

	/**
	 * 一致性检查, 主动检查本地数据与zk中心数据的一致性, 防止出现因连接中断而丢失更新消息, 默认开启
	 */
	private boolean consistencyCheck = true;

	/**
	 * 检查频率, in milliseconds
	 */
	private long consistencyCheckRate = 60 * 1000;

	public ZookeeperConfigProfile(final String connectStr, final String rootNode, final boolean openLocalCache) {
		this(connectStr, rootNode, null, openLocalCache, new ExponentialBackoffRetry(100, 2));
	}

	public ZookeeperConfigProfile(final String connectStr, final String rootNode, final String version) {
		this(connectStr, rootNode, version, false, new ExponentialBackoffRetry(100, 2));
	}

	public ZookeeperConfigProfile(final String connectStr, final String rootNode, final String version, final boolean openLocalCache,
			final RetryPolicy retryPolicy) {
		super(version);
		this.connectStr = Preconditions.checkNotNull(connectStr);
		this.rootNode = Preconditions.checkNotNull(rootNode);
		this.openLocalCache = openLocalCache;
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

	public final boolean isConsistencyCheck() {
		return consistencyCheck;
	}

	public final void setConsistencyCheck(boolean consistencyCheck) {
		this.consistencyCheck = consistencyCheck;
	}

	public final long getConsistencyCheckRate() {
		return consistencyCheckRate;
	}

	public final void setConsistencyCheckRate(long consistencyCheckRate) {
		this.consistencyCheckRate = consistencyCheckRate;
	}

	public final boolean isOpenLocalCache() {
		return openLocalCache;
	}

	public final String getLocalCacheFolder() {
		return localCacheFolder;
	}

	public final void setLocalCacheFolder(String localCacheFolder) {
		this.localCacheFolder = localCacheFolder;
	}

	public String getVersionedRootNode() {
		if(Strings.isNullOrEmpty(version)){
			return rootNode;
		}
		return ZKPaths.makePath(rootNode, version);
	}

	@Override
	public String toString() {
		return "ConfigProfile [connectStr=" + connectStr + ", rootNode=" + rootNode + ", version=" + version + ", retryPolicy=" + retryPolicy
				+ ", openLocalCache=" + openLocalCache + ", localCacheFolder=" + localCacheFolder + ", consistencyCheck=" + consistencyCheck
				+ ", consistencyCheckRate=" + consistencyCheckRate + "]";
	}

}
