[例1] 同步数据不覆盖，遇到已存在节点跳过
java -jar zk-sync.jar zk1-host:2181 zk2-host:2181 /aaa/bbb
[例2] 同步数据并覆盖已存在节点
java -jar zk-sync.jar zk1-host:2181 zk2-host:2181 /aaa/bbb 1