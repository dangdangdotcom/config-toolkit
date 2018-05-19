package com.dangdang.config.service.easyzk.demo.simple;

import com.dangdang.config.service.easyzk.demo.Const;
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
        ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile(Const.ZK, rootNode, "1.0.0", true);
        ZookeeperConfigGroup propertyGroup1 = new ZookeeperConfigGroup(configProfile, "property-group1");
        propertyGroup1.close();
    }

}
