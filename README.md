# 分布配置工具包

Config Toolkit提供了一层对配置资源的抽象，配置可以从多种介质加载，工具内部提供了对zookeeper/本地文件/远程http文件的支持，并提供了SPI接口支持用户扩展自定义介质

<hr>

### 社区
* Group: https://groups.yahoo.com/group/config-toolkit
* QQ群: 457997852
* Config Toolkit Go语言版本：https://github.com/mbaight/config-toolkit-go
* Zookeeper Java Shell: https://github.com/crnlmchina/zk-util

### 文档
文档: [https://github.com/dangdangdotcom/config-toolkit/wiki](https://github.com/dangdangdotcom/config-toolkit/wiki "https://github.com/dangdangdotcom/config-toolkit/wiki")

Release Note: [https://github.com/dangdangdotcom/config-toolkit/wiki/1.-release-notes](https://github.com/dangdangdotcom/config-toolkit/wiki/1.-release-notes "https://github.com/dangdangdotcom/config-toolkit/wiki/1.-release-notes")

### 搭建配置界面

#### 使用docker镜像运行config-face
```
docker run -it -d -e "zk=localhost:2181" -p 8080:8080 crnlmchina/config-face:v3.3.0
```

#### 或者下载源码编译config-face
```
git clone https://github.com/dangdangdotcom/config-toolkit.git
cd config-toolkit/config-face
mvn package
java -jar config-face.jar --zk=localhost:2181
```

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
 - 访问http://localhost:8080/
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
  <version>3.3.2-RELEASE</version>
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
spring xml schema
```
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:config="https://crnlmchina.github.io/config"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
             https://crnlmchina.github.io/config https://crnlmchina.github.io/config/config.xsd">
```
bean配置
```
<config:profile connect-str="localhost:2181" root-node="/projectx/modulex" 
		version="1.0.0"/>

<config:placeholder>
	<config:group node="property-group1" />	
	<config:group node="property-group2" />	
</config:placeholder>

<!-- Your business bean -->
<bean class="your.BusinessBean">
    <property name="strProp" value="${config.str}" />
    <property name="intProp" value="${config.int}" />
</bean>
```
由于spring对多个placeholder的支持不太好，需要仔细配置order，所以建议使用SPEL方式来配置
#### 结合spring SPEL方式注入配置
```
<config:profile connect-str="localhost:2181" root-node="/projectx/modulex" version="1.0.0"/>
<config:group id="groupProp" node="group"/>

<!-- Your business bean -->
<bean class="your.BusinessBean">
    <property name="strProp" value="#{groupProp['config.str']}" />
    <property name="intProp" value="#{groupProp['config.int']}" />
</bean>
```

### 更多
如果您已经读到这儿了，辛苦回到页首给项目点个star吧，让更多的人可以关注到它 -_-
