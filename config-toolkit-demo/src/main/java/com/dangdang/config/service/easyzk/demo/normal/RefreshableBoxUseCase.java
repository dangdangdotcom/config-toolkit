package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.easyzk.demo.ExampleBean;
import com.dangdang.config.service.sugar.RefreshableBox;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class RefreshableBoxUseCase {

	public static void main(String[] args) {
		ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("zoo.host1:8181", "/projectx/modulex", "1.0.0");
		GeneralConfigGroup node = new ZookeeperConfigGroup(configProfile, "property-group1");
		
		RefreshableBox<ExampleBean> box = new RefreshableBox<ExampleBean>(node) {

			@Override
			protected ExampleBean doInit(GeneralConfigGroup node) {
				ExampleBean bean = new ExampleBean(node.get("string_property_key"), Integer.parseInt(node.get("int_property_key")));
				bean.setCool(Boolean.parseBoolean(node.get("cool")));
				return bean;
			}
		};

		for (int i = 0; i < 10000; i++) {
			System.out.println(box.getObj());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
