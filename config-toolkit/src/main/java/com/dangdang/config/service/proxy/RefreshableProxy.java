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
package com.dangdang.config.service.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 可刷新代理对象的动态代理
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 * @param <T>
 *            代理对象
 */
public class RefreshableProxy<T> implements MethodInterceptor {

	private T target;
	
	private final T proxy;

	@SuppressWarnings("unchecked")
	public RefreshableProxy(final T target) {
		super();
		this.target = target;
		final Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		enhancer.setCallback(this);
		proxy = (T) enhancer.create();
	}

	public void refresh(final T target) {
		this.target = target;
	}

	public T getInstance() {
		return proxy;
	}

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		return proxy.invoke(target, args);
	}

}
