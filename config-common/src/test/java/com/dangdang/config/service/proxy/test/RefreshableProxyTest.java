package com.dangdang.config.service.proxy.test;

import org.junit.Assert;
import org.junit.Test;

import com.dangdang.config.service.proxy.RefreshableProxy;

public class RefreshableProxyTest {

	public static class Person {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Test
	public void testRefresh() {
		Person p = new Person();
		p.setName("wyx");

		RefreshableProxy<Person> refreshableProxy = new RefreshableProxy<Person>(p);
		Person proxy = refreshableProxy.getInstance();

		Assert.assertEquals("wyx", proxy.getName());

		Person p2 = new Person();
		p2.setName("Yuxuan");
		refreshableProxy.refresh(p2);

		Assert.assertEquals("Yuxuan", proxy.getName());

	}

}
