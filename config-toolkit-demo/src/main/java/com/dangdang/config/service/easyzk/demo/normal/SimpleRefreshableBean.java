package com.dangdang.config.service.easyzk.demo.normal;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.easyzk.demo.simple.ExampleBean;
import com.dangdang.config.service.sugar.RefreshableBox;
import com.google.common.collect.Lists;

/**
 * Created by yuxuanwang on 2017/5/16.
 */
public class SimpleRefreshableBean extends RefreshableBox<ExampleBean> {


    public SimpleRefreshableBean(GeneralConfigGroup node) {
        // 当version属性变化时更新bean
        super(node, Lists.newArrayList("version"));
    }

    @Override
    protected ExampleBean doInit(GeneralConfigGroup node) {
        ExampleBean bean = new ExampleBean(node.get("string_property_key"), Integer.parseInt(node.get("int_property_key")));
        bean.setCool(Boolean.parseBoolean(node.get("cool")));
        return bean;
    }
}
