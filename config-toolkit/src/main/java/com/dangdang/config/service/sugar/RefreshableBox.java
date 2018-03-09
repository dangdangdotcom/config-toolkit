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
package com.dangdang.config.service.sugar;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.observer.IObserver;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 根据属性变化自刷新的容器
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class RefreshableBox<T> implements IObserver {

	/**
	 * 真实对象
	 */
	private T obj;

	/**
	 * 会影响真实对象的属性值，为空时代表任意属性变化都会刷新对象
	 */
	private List<String> propertyKeysCare;

	private GeneralConfigGroup node;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public RefreshableBox(GeneralConfigGroup node, List<String> propertyKeysCare) {
		this.node = node;
		this.propertyKeysCare = propertyKeysCare;
		node.register(this);
		init();
	}

	public RefreshableBox(GeneralConfigGroup node) {
		this(node, null);
	}

	private void init() {
		lock.writeLock().lock();
		try {
			obj = doInit(node);
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected abstract T doInit(GeneralConfigGroup node);

	public T getObj() {
		lock.readLock().lock();
		try {
			return obj;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void notified(String data, String value) {
		if (propertyKeysCare == null || propertyKeysCare.isEmpty() || propertyKeysCare.contains(data)) {
			init();
		}
	}

}
