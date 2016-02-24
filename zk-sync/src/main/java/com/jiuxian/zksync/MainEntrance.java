package com.jiuxian.zksync;

import java.util.Arrays;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

public class MainEntrance {

	/**
	 * Sync zk data
	 * 
	 * [例1] 同步数据不覆盖，遇到已存在节点跳过<br>
	 * java -jar zk-sync.jar zk1-host:2181 zk2-host:2181 /aaa/bbb<br>
	 * 
	 * [例2] 同步数据并覆盖已存在节点<br>
	 * java -jar zk-sync.jar zk1-host:2181 zk2-host:2181 /aaa/bbb 1
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("Invalid params: " + Arrays.toString(args));
			System.out.println("Valid format: source:port target:port /root/node [overwrite(0/1)]");
			System.exit(1);
		}

		String source = args[0];
		String target = args[1];
		String rootNode = args[2];
		String overwrite = args.length > 3 ? args[3] : "0";

		try (CuratorFramework sourceClient = CuratorFrameworkFactory.newClient(source, new RetryOneTime(1))) {
			sourceClient.start();
			try (CuratorFramework targetClient = CuratorFrameworkFactory.newClient(target, new RetryOneTime(1))) {
				targetClient.start();
				try {
					Stat stat = sourceClient.checkExists().forPath(rootNode);
					if (stat != null) {

						if (targetClient.checkExists().forPath(rootNode) == null) {
							System.out.println("Create root node: " + rootNode);
							targetClient.create().creatingParentsIfNeeded().forPath(rootNode, sourceClient.getData().forPath(rootNode));
						}

						syncChildren(rootNode, sourceClient, targetClient, "1".equals(overwrite));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void syncChildren(String rootNode, CuratorFramework sourceClient, CuratorFramework targetClient, boolean overwrite)
			throws Exception {
		List<String> children = sourceClient.getChildren().forPath(rootNode);
		if (children != null) {
			for (String child : children) {
				String path = ZKPaths.makePath(rootNode, child);
				System.out.println("Sync node: " + path);
				byte[] data = sourceClient.getData().forPath(path);
				Stat stat = targetClient.checkExists().forPath(path);
				if (stat == null) {
					targetClient.create().forPath(path, data);
				} else if (overwrite) {
					targetClient.setData().forPath(path, data);
				}

				syncChildren(path, sourceClient, targetClient, overwrite);
			}
		}
	}

}
