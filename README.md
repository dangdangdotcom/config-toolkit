# 分布配置工具包

<h1>Config Toolkit V3.1.0 RELEASED</h1>

### 在大型集群和分布式应用中，配置不宜分散到集群结点中，应该集中管理.

<hr>

### 依赖
* JAVA 7+
* TOMCAT 7+ for ConfigWeb

### RELEASES
Release notes: [https://github.com/dangdangdotcom/config-toolkit/wiki/release-notes](https://github.com/dangdangdotcom/config-toolkit/wiki/release-notes "Release Notes")

Old doc: [V2.x document](https://github.com/dangdangdotcom/config-toolkit/wiki/v2doc "V2.x document")

### 模块
* Config Toolkit - 封装应用属性配置的获取及更新
* ConfigWeb - 提供web界面维护属性配置，提供配置导入导出功能

### 特性
* 集中管理集群配置
* 实现配置热更新
* 多配置源支持，内置支持zookeeper、本地文件、http协议
* Spring集成
* 本地配置覆盖
* 配置管理web界面
* 版本控制，支持灰度发布
* 支持为配置项添加注释

### Quick Start
#### load properties from zookeeper
<pre><code>
    ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("zoo.host1:8181", "/projectx/modulex", "1.0.0");
    GeneralConfigGroup propertyGroup1 = new ZookeeperConfigGroup(configProfile, "property-group1");
</code></pre>

#### load properties from classpath file
<pre><code>
    FileConfigProfile configProfile = new FileConfigProfile("UTF8", "properties");
    ConfigGroup configGroup = new FileConfigGroup(configProfile, "classpath:property-group1.properties");
</code></pre>

#### load xml properties from classpath file
<pre><code>
    FileConfigProfile configProfile = new FileConfigProfile("UTF8", "xml");
    ConfigGroup configGroup = new FileConfigGroup(configProfile, "classpath:property-group1.xml");
</code></pre>

#### load properties from file
<pre><code>
    FileConfigProfile configProfile = new FileConfigProfile("UTF8", "properties");
    ConfigGroup configGroup = new FileConfigGroup(configProfile, "file:/Users/yuxuanwang/Work/git/config-toolkit/config-toolkit-demo/src/main/resources/property-group1.properties");
</code></pre>

#### load properties from http
<pre><code>
    FileConfigProfile configProfile = new FileConfigProfile("UTF8", "properties");
    ConfigGroup configGroup = new FileConfigGroup(configProfile, "http://crnlmchina.github.io/config-group.properties");
</code></pre>

### 词典
* ConfigProfile - 配置参数,根据不同的资源类型加载配置组
* ConfigGroup - 配置组

### 使用
#### maven依赖
<pre><code>
    &lt;dependency&gt;
      &lt;groupId&gt;com.dangdang&lt;/groupId&gt;
      &lt;artifactId&gt;config-toolkit&lt;/artifactId&gt;
      &lt;version&gt;3.1.0-RELEASE&lt;/version&gt;
    &lt;/dependency&gt;
</code></pre>

#### 核心概念

ConfigGroup封装一组配置数据，负载加载指定配置组在资源中的数据.

ConfigGroup封装了数据源的变化事件，从ConfigGroup中获取的配置数据永远是最新的.

为了减弱Config Toolkit的侵入性，ConfigGroup继承自Map<String,String>，使用者可以将ConfigGroup以Map的形式注入到业务bean中.

#### Spring PlaceholderConfigurer集成

Spring的使用者一般使用PlaceHolder加载properties文件，Config Toolkit提供类ZookeeperSourceFactory来兼容Spring Placeholder.

需要注意的是，由于Spring Placeholder在bean的初始化阶段将配置信息注入到bean中，所以使用这种方式无法获取配置热更新的特性.

并不是所有的配置都有必要实现热更新，比如数据库的连接池，重新初始化的成本比较高；比较适合使用热更新的场景是一些需要在线调整的业务参数.
<pre><code>
    &lt;bean id="configProfile" class="com.dangdang.config.service.zookeeper.ZookeeperConfigProfile"&gt;
    	&lt;constructor-arg name="connectStr" value="zoo.host1:8181,zoo.host2:8181,zoo.host3:8181" /&gt;
		&lt;constructor-arg name="rootNode" value="/projectx/modulex" /&gt;
        &lt;constructor-arg name="version" value="1.0.0" /&gt;
	&lt;/bean&gt;

	&lt;bean id="configGroupSources" class="com.dangdang.config.service.support.spring.ConfigGroupSourceFactory" factory-method="create"&gt;
		&lt;constructor-arg name="configGroups"&gt;
			&lt;list&gt;
                &lt;bean class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup" c:configProfile-ref="configProfile" c:node="property-group1" /&gt;
                &lt;bean class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup" c:configProfile-ref="configProfile" c:node="property-group1" /&gt;
			&lt;/list&gt;
		&lt;/constructor-arg&gt;
	&lt;/bean&gt;

	&lt;bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer"&gt;
		&lt;property name="order" value="1" /&gt;
		&lt;property name="ignoreUnresolvablePlaceholders" value="true" /&gt;
		&lt;property name="propertySources" ref="configGroupSources" /&gt;
	&lt;/bean&gt;
</code></pre>

#### Spring SPEL集成

由于ConfigGroup是Map的实现类，在SPEL表达式的支持范围中，可以直接使用`#{systemProperties.myProp}`的格式在XML文件或@Value中将属性值注入到bean中.

SPEL同样无法获取热更新的特性.

旧util properties用法：
<pre><code>
	&lt;util:properties id="configToolkitCommon" location="classpath:config-toolkit.properties" /&gt;
</code></pre>
Config-toolkit支持：
<pre><code>
	&lt;bean id="configToolkitCommon" class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup"&gt;
        &lt;constructor-arg name="configProfile" ref="configProfile" /&gt;
		&lt;constructor-arg name="node" value="config-toolkit" /&gt;
	&lt;/bean&gt;
</code></pre>

由于spring的限制，如果你需要将`ConfigGroup`以`Map<String, String>`的方式注入到业务bean中时，也需要使用SPEL或@Resource
<pre><code>
@Resource
private Map&lt;String, String&gt; configGroup;

or

@Value('#{configGroup}')
private Map&lt;String, String&gt; configGroup;
</code></pre>

#### 配置覆盖

不同的配置组之间可以任意互相覆盖.

[例]通过本地配置文件覆盖zookeeper配置：
<pre><code>
    ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("zoo.host1:8181", "/projectx/modulex", "1.0.0");
    ConfigGroup zkConfigGroup = new ZookeeperConfigGroup(configProfile , "property-group1");
    FileConfigProfile fileConfigProfile = new FileConfigProfile("UTF8", "properties");
ConfigGroup configGroup = new FileConfigGroup(zkConfigGroup, fileConfigProfile, "classpath:config-group1.properties");
</code></pre>

### SPI

#### 扩展配置源

实现`com.dangdang.config.service.ConfigGroup`接口

#### 扩展文件协议

实现`com.dangdang.config.service.file.protocol.Protocol`接口

添加配置文件`META-INF/toolkit/com.dangdang.config.service.file.protocol.Protocol`

内容格式为：`xxprotocol=xx.XxProtocol`

Config Toolkit内置实现：
<pre><code>
classpath=com.dangdang.config.service.file.protocol.ClasspathProtocol
file=com.dangdang.config.service.file.protocol.FileProtocol
http=com.dangdang.config.service.file.protocol.HttpProtocol
https=com.dangdang.config.service.file.protocol.HttpProtocol
</code></pre>

#### 扩展文件类型

实现`com.dangdang.config.service.file.contenttype.ContentType`接口

添加配置文件`META-INF/toolkit/com.dangdang.config.service.file.contenttype.ContentType`

内容格式为：`xxContentType=xx.XxContentType`

Config Toolkit内置实现：
<pre><code>
properties=com.dangdang.config.service.file.contenttype.PropertiesContentType
xml=com.dangdang.config.service.file.contenttype.XmlContentType
</code></pre>

### Config Web 管理界面

Config Web 提供界面方便管理zookeeper中的配置数据.

为避免误操作，避免开发人员获取线上配置的修改权限.

鉴权密码为节点的值，使用SHA1 HEX字符串加密，需要运维手动修改zookeeper数据创建应用根节点密码.

一般linux系统都带有python，可以使用python脚本方便生成：


> `python -c "import hashlib;print hashlib.sha1('abc').hexdigest();"`
> 
> `# a9993e364706816aba3e25717850c26c9cd0d89d`
> 
> `echo "set /aaa/bbb a9993e364706816aba3e25717850c26c9cd0d89d" |./zkCli.sh -server localhost:2181`

![Config Web Snapshot](http://crnlmchina.github.io/config-web2.jpg)