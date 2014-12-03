# 分布配置工具包

#### 在大型集群和分布式应用中，配置同样需要复用，如果配置分布在集群结点中，修改同步就比较麻烦，而且比较容易出错。

<hr>

#### 依赖
* JAVA 7

#### 模块
* EasyZk - 封装属性配置的获取及更新
* ConfigWeb - 提供web界面方便修改属性配置及迁移

#### 特性
* 集中管理集群配置
* 实现配置热更新
* Spring集成
* 配置管理web界面

#### 使用
- 直接使用
<pre><code>
	ConfigFactory configFactory = new ConfigFactory(connectStr, rootNode);
	ConfigNode node = configFactory.getConfigNode("group0");
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
