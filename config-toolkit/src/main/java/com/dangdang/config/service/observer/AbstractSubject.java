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
package com.dangdang.config.service.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题通用实现
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class AbstractSubject implements ISubject {

	/**
	 * 观察者列表
	 */
	private final List<IObserver> watchers = new ArrayList<>();

	@Override
	public void register(final IObserver watcher) {
		if(watcher == null) {
			throw new IllegalArgumentException("watcher cannot be null");
		}
		watchers.add(watcher);
	}

	@Override
	public void notify(final String key, final String value) {
		for (final IObserver watcher : watchers) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					watcher.notified(key, value);
				}
			}).start();
		}
	}

}
