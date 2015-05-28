# Config Toolkit

<h1>Config Toolkit V3.1.0 RELEASED</h1>

### In a large cluster and distributed application, configuration should not be dispersed in the cluster nodes, but centrally managed.

<hr>

### Depends
* JAVA 7+
* TOMCAT 7+ for ConfigWeb

### RELEASES
Release notes: [https://github.com/dangdangdotcom/config-toolkit/wiki/release-notes](https://github.com/dangdangdotcom/config-toolkit/wiki/release-notes "Release Notes")

Old doc: [V2.x document](https://github.com/dangdangdotcom/config-toolkit/wiki/v2doc "V2.x document")

### Modules
* Config Toolkit - Encapsulate the access of the configurations for applications
* ConfigWeb - Configuration GUI

### Features
* Centralized management cluster configuration
* Hot update of configurations
* Multi source support, built-in support for zookeeper、local file and HTTP.
* Spring integration
* Overrides in configuration sources
* Configuration GUI
* Version control, support gated launch
* Comments support

### Thanks
* 翻译 - 周赟

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

### Dictionary
* ConfigProfile - Configuration parameters for configuration sources
* ConfigGroup - A group of configs

### Usage
#### maven dependency
<pre><code>
    &lt;dependency&gt;
      &lt;groupId&gt;com.dangdang&lt;/groupId&gt;
      &lt;artifactId&gt;config-toolkit&lt;/artifactId&gt;
      &lt;version&gt;3.1.0-RELEASE&lt;/version&gt;
    &lt;/dependency&gt;
</code></pre>

#### Core concepts

ConfigGroup encapsulates a set of configuration data, and loads the data in specified source.

ConfigGroup encapsulates the changes of the data sources，guarantees that the configurations in ConfigGroup are always be the latest.

In order to weaken the invasive of Config Toolkit，ConfigGroup inherited from Map，and users can inject ConfigGroup into the business bean by the form of Map.

#### Spring PlaceholderConfigurer integration

Spring users generally use PlaceHolder to load the properties files，Config Toolkit provides ZookeeperSourceFactory class to compatible with Spring Placeholder.

Note that the Spring Placeholder injected the configuration information into bean at initialization stage, in this way the feature of the configuration hot update is unavailable.

Not all configurations are necessary to be hot updated，such as database connection pool, the cost of re-initialization is high; the suggested use case for hot update is the business parameters which often needs adjustment.
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

#### Spring SPEL integration

Since ConfigGroup is implementation of Map, and is supported by SPEL expressions, you can use the #{systemProperties.myProp} format to inject attribute values into bean in the XML file or @Value.

With SPEL the feature of hot update is unvailable also.

Old util properties style：
<pre><code>
	&lt;util:properties id="configToolkitCommon" location="classpath:config-toolkit.properties" /&gt;
</code></pre>
Config-toolkit style：
<pre><code>
	&lt;bean id="configToolkitCommon" class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup"&gt;
        &lt;constructor-arg name="configProfile" ref="configProfile" /&gt;
		&lt;constructor-arg name="node" value="config-toolkit" /&gt;
	&lt;/bean&gt;
</code></pre>

As spring's limitation，if you need inject `ConfigGroup` as `Map<String, String>` to beans，you must use SPEL or @Resource
<pre><code>
@Resource
private Map&lt;String, String&gt; configGroup;

or

@Value('#{configGroup}')
private Map&lt;String, String&gt; configGroup;
</code></pre>

#### Overrides

Configurations can override each other.

[For example] Override zookeeper configs with local file configs：
<pre><code>
    ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("zoo.host1:8181", "/projectx/modulex", "1.0.0");
    ConfigGroup zkConfigGroup = new ZookeeperConfigGroup(configProfile , "property-group1");
    FileConfigProfile fileConfigProfile = new FileConfigProfile("UTF8", "properties");
ConfigGroup configGroup = new FileConfigGroup(zkConfigGroup, fileConfigProfile, "classpath:config-group1.properties");
</code></pre>

### SPI

#### ConfigGroup SPI

Implements `com.dangdang.config.service.ConfigGroup` interface

#### File procotol SPI

Implements `com.dangdang.config.service.file.protocol.Protocol` interface

Create file `META-INF/toolkit/com.dangdang.config.service.file.protocol.Protocol`

Content format：`xxprotocol=xx.XxProtocol`

Config Toolkit internal implements：
<pre><code>
classpath=com.dangdang.config.service.file.protocol.ClasspathProtocol
file=com.dangdang.config.service.file.protocol.FileProtocol
http=com.dangdang.config.service.file.protocol.HttpProtocol
https=com.dangdang.config.service.file.protocol.HttpProtocol
</code></pre>

#### File ContentType SPI

Implements `com.dangdang.config.service.file.contenttype.ContentType` interface

Create file `META-INF/toolkit/com.dangdang.config.service.file.contenttype.ContentType`

Content format：`xxContentType=xx.XxContentType`

Config Toolkit internal implements：
<pre><code>
properties=com.dangdang.config.service.file.contenttype.PropertiesContentType
xml=com.dangdang.config.service.file.contenttype.XmlContentType
</code></pre>

### Config Web

Config Web provides GUI interface to manage the configuration data in zookeeper easily.

Password is SHA1-HEXed.

Initial scripts：


> `python -c "import hashlib;print hashlib.sha1('abc').hexdigest();"`
> 
> `# a9993e364706816aba3e25717850c26c9cd0d89d`
> 
> `echo "set /aaa/bbb a9993e364706816aba3e25717850c26c9cd0d89d" |./zkCli.sh -server localhost:2181`

![Config Web Snapshot](http://crnlmchina.github.io/config-web2.jpg)