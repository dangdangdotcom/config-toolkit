package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.easyzk.ConfigFactory;
import com.dangdang.config.service.easyzk.ConfigNode;
import com.dangdang.config.service.easyzk.demo.ExampleBean;
import com.dangdang.config.service.easyzk.sugar.RefreshableBox;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class RefreshableBoxUseCase {

	public static void main(String[] args) {
		ConfigNode node = new ConfigFactory("zoo.host1:8181", "/projectx/modulex", "1.0.0").getConfigNode("property-group1");
		RefreshableBox<ExampleBean> box = new RefreshableBox<ExampleBean>(node) {

			@Override
			protected ExampleBean doInit(ConfigNode node) {
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
