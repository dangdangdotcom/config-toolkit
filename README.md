# 分布配置工具包

<h2>${rootNode}/${version}/${group}/${keyValues}</h2>

<hr>

### 社区
* QQ群: 457997852

### 文档
文档: [https://github.com/dangdangdotcom/config-toolkit/wiki](https://github.com/dangdangdotcom/config-toolkit/wiki "https://github.com/dangdangdotcom/config-toolkit/wiki")

### 搭建配置界面
#### 下载config-toolkit项目
```
git clone https://github.com/dangdangdotcom/config-toolkit.git
cd config-toolkit/config-zk-web
mvn package
```
将编译好的config-web.war部署到tomcat即可
#### 创建初始权限配置
使用命令行创建zookeeper配置根节点，根节点密码使用sha1加密，如果要使用明文密码，可以自行修改config-zk-web的鉴权部分代码
以根路径为`/projectx/modulex`密码为`abc`为例
```
python -c "import hashlib;print hashlib.sha1('abc').hexdigest();"
# a9993e364706816aba3e25717850c26c9cd0d89d 
zkCli.sh -server localhost:2181
create /projectx 1
create /projectx/modulex a9993e364706816aba3e25717850c26c9cd0d89d
```
#### 登录config-web，创建示例配置
 - 访问http://localhost:8080/config-web
 - 点击"切换根节点"，输入/projectx/modulex，密码abc
 - 点击"新建版本"，输入1.0.0
 - 左侧的组管理，输入group，点击"创建"
 - 在右侧添加两个配置，分别为config.str=hello, config.int=7758

### 项目中加载配置
#### 添加maven依赖
```
<dependency>
  <groupId>com.dangdang</groupId>
  <artifactId>config-toolkit</artifactId>
  <version>3.1.7-RELEASE</version>
</dependency>
```
#### 使用Java代码直接获取配置
```
ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("localhost:2181", "/projectx/modulex", "1.0.0");
GeneralConfigGroup group = new ZookeeperConfigGroup(configProfile, "group");

String stringProperty = group.get("config.str");
Preconditions.checkState("hello".equals(stringProperty));
String intProperty = group.get("config.int");
Preconditions.checkState(7758 == Integer.parseInt(intProperty));
```
#### 结合spring placeholder方式注入配置
```
<bean id="configProfile" class="com.dangdang.config.service.zookeeper.ZookeeperConfigProfile">
    <constructor-arg name="connectStr" value="localhost:2181" />
    <constructor-arg name="rootNode" value="/projectx/modulex" />
    <constructor-arg name="version" value="1.0.0" />
</bean>

<bean id="configGroupSources" class="com.dangdang.config.service.support.spring.ConfigGroupSourceFactory" factory-method="create">
    <constructor-arg name="configGroups">
        <list>
            <bean class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup" c:configProfile-ref="configProfile" c:node="-group" />
        </list>
    </constructor-arg>
</bean>

<bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
    <property name="order" value="1" />
    <property name="ignoreUnresolvablePlaceholders" value="true" />
    <property name="propertySources" ref="configGroupSources" />
</bean>

<!-- Your business bean -->
<bean class="your.BusinessBean">
    <property name="strProp" value="${config.str}" />
    <property name="intProp" value="${config.int}" />
</bean>
```
由于spring对多个placeholder的支持不太好，需要仔细配置order，所以建议便宜SPEL方式来配置
#### 结合spring SPEL方式注入配置
```
<bean id="configProfile" class="com.dangdang.config.service.zookeeper.ZookeeperConfigProfile">
    <constructor-arg name="connectStr" value="localhost:2181" />
    <constructor-arg name="rootNode" value="/projectx/modulex" />
    <constructor-arg name="version" value="1.0.0" />
</bean>
<bean id="groupProp" class="com.dangdang.config.service.zookeeper.ZookeeperConfigGroup">
    <constructor-arg name="configProfile" ref="configProfile" />
    <constructor-arg name="node" value="group" />
</bean>

<!-- Your business bean -->
<bean class="your.BusinessBean">
    <property name="strProp" value="#{groupProp['config.str']}" />
    <property name="intProp" value="#{groupProp['config.int']}" />
</bean>
```
