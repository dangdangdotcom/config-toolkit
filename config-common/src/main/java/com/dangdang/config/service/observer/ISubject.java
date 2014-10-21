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

/**
 * 被观察者关心的主题
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 *         通知的数据类型
 */
public interface ISubject {

	/**
	 * 注册观察者
	 * 
	 * @param watcher
	 */
	void register(IObserver watcher);

	/**
	 * 通知观察者
	 * 
	 * @param key
	 * @param value
	 */
	void notify(String key, String value);

}
