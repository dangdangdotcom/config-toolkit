# 分布配置工具包

#### 在大型集群和分布式应用中，配置同样需要复用，如果配置分布在集群结点中，修改同步就比较麻烦，而且比较容易出错。

<hr>

#### 依赖
* JAVA 6+
* TOMCAT 7+

#### 模块
* EasyZk - 封装从应用到zookeeper之间属性配置的获取及更新
* ConfigWeb - 提供web界面方便维护属性配置及遗留系统properties文件迁移，提供配置导入导出功能

#### 特性
* 集中管理集群配置
* 实现配置热更新
* Spring集成
* 本地配置覆盖
* 配置管理web界面

#### 词典
* ConfigFactory - 配置工厂，用于定义zookeeper地址及配置根目录
* ConfigNode - 配置组，一组配置信息，对应于遗留系统中的properties属性文件

#### 使用
- maven
<pre><code>
    &lt;dependency&gt;
      &lt;groupId&gt;com.dangdang&lt;/groupId&gt;
      &lt;artifactId&gt;config-toolkit-easyzk&lt;/artifactId&gt;
      &lt;version&gt;1.2.0-RELEASE&lt;/version&gt;
    &lt;/dependency&gt;
</code></pre>
- 直接使用
<pre><code>
    // 创建配置工厂指向zk的地址及配置在zk中的根地址，映射到zookeeper中的/projectx/modulex
	ConfigFactory configFactory = new ConfigFactory("zoo.host1:8181,zoo.host2:8181,zoo.host3:8181", "/projectx/modulex");
    // 从工厂中加载某配置组，映射到zookeeper中的/projectx/modulex/group0
	ConfigNode node = configFactory.getConfigNode("group0");
    // 从配置组中获取某配置，映射到zookeeper中的/projectx/modulex/group0/name
	Assert.assertNotNull(node.getProperty("name"));
</code></pre>

- Spring PlaceholderConfigurer集成
<pre><code>
	&lt;bean id="configFactory" class="com.dangdang.config.service.easyzk.ConfigFactory" factory-method="create"&gt;
		&lt;constructor-arg name="connectStr" value="zoo.host1:8181,zoo.host2:8181,zoo.host3:8181" /&gt;
		&lt;constructor-arg name="rootNode" value="/projectx/modulex" /&gt;
	&lt;/bean&gt;

	&lt;bean id="zookeeperSources" class="com.dangdang.config.service.easyzk.support.spring.ZookeeperSourceFactory" factory-method="create"&gt;
		&lt;constructor-arg name="configFactory" ref="configFactory" /&gt;
		&lt;constructor-arg name="nodes"&gt;
			&lt;list&gt;
				&lt;value&gt;config-group1&lt;/value&gt;
				&lt;value&gt;config-group2&lt;/value&gt;
				&lt;value&gt;config-group3&lt;/value&gt;
			&lt;/list&gt;
		&lt;/constructor-arg&gt;
	&lt;/bean&gt;

	&lt;bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer"&gt;
		&lt;property name="order" value="1" /&gt;
		&lt;property name="ignoreUnresolvablePlaceholders" value="true" /&gt;
		&lt;property name="propertySources" ref="zookeeperSources" /&gt;
	&lt;/bean&gt;
</code></pre>

- 本地配置覆盖(一般用于调试集群中的单点)

在classpath下添加本地配置文件,格式为XML,默认为local-override.xml,可以通过指定环境变量来修改
`-Dlocal-override.file=yourfile.xml`<br/>
[例]：
<pre><code>
	&lt;?xml version="1.0" encoding="UTF-8"?&gt;
	&lt;node-factories xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://crnlmchina.github.io/local-override.xsd"&gt;
		&lt;node-factory root="/projectx/modulex"&gt;
			&lt;group id="property-group1"&gt;
				&lt;node key="string_property_key"&gt;Welcome here.&lt;/node&gt;
			&lt;/group&gt;
		&lt;/node-factory&gt;
	&lt;/node-factories&gt;
</code></pre>

以上的配置会覆盖zookeeper中`/projectx/modulex/property-group1/string_property_key`的值为`Welcome here.`
