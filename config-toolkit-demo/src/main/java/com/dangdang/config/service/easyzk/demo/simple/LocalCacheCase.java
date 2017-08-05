package com.dangdang.config.service.easyzk.demo.simple;

import com.dangdang.config.service.zookeeper.ConfigLocalCache;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 */
public class LocalCacheCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String rootNode = "/projectx/modulex";
        ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("192.168.5.99:2181", rootNode, "1.0.0", true);
        ZookeeperConfigGroup propertyGroup1 = new ZookeeperConfigGroup(configProfile, "property-group1");
        propertyGroup1.close();
    }

}
