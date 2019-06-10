# debbie
for microservice

## 说明
    该项目使用Java11开发，为什么不适用kotlin，因为jetbrain开发的ktor框架已经很好用了啊（[滑稽]）；
    项目包管理采用gradle dsl。
    项目力求最简化，简单来说，就是能用自己写的尽量自己写；其次要模块界限分明，包引用简单化；
    使用spi而不是通过optional来引用其他包或项目，不会因为包引入给开发者带来困扰。

## construction


## dependence
#### core: 
    org.slf4j:slf4j-api:1.7.26
    com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8

#### jdbc:
    com.truthbean.debbie:debbie-core:current
    org.slf4j:slf4j-api:1.7.26

#### mvc:
    com.truthbean.debbie:debbie-core:current
    org.slf4j:slf4j-api:1.7.26

#### servlet: 
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-mvc:current

    jakarta.servlet:jakarta.servlet-api:4.0.2
    jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:1.2.4
    org.glassfish.web:javax.servlet.jsp.jstl:1.2.4
    commons-fileupload:commons-fileupload:1.4
    
#### httpclient:
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-mvc:current

#### zaxxer:
    com.truthbean.debbie:debbie-jdbc:current
    com.zaxxer:HikariCP:3.3.1
    
#### server:
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-mvc:current
    
#### tomcat:
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-servlet:current
    com.truthbean.debbie:debbie-server:current
    
    org.apache.tomcat.embed:tomcat-embed-core:9.0.19
    org.apache.tomcat.embed:tomcat-embed-jasper:9.0.19
    org.apache.tomcat.embed:tomcat-embed-el:9.0.19
    
#### undertow:
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-server:current
    
    io.undertow:undertow-core:2.0.20.Final
    
#### netty:
    org.slf4j:slf4j-api:1.7.26
    com.truthbean.debbie:debbie-server:current
    
    io.netty:netty-all:4.1.36.Final

### package引用规范
尽量使用compileOnly，不要给开发者带来额外的包引用负担

### properties规范
properties名称一律小写，第三方框架的properties的key由驼峰换成“-”分割

@PropertyInject 标识的 field，通过setter方法来 注入的

## maven

### server
```xml
<dependency>
    <groupId>com.truthbean.debbie</groupId>
    <artifactId>debbie-undertow</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.truthbean.debbie</groupId>
    <artifactId>debbie-netty</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.truthbean.debbie</groupId>
    <artifactId>debbie-tomcat</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```

### jdbc
```xml
<dependency>
    <groupId>com.truthbean.debbie</groupId>
    <artifactId>debbie-jdbc</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```
```xml
<dependency>
    <groupId>com.truthbean.debbie</groupId>
    <artifactId>debbie-hikari</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```

## 目标
--------
~~1.先实现一个mvc内核~~

~~2.用servlet实现功能~~

~~3.集成一个tomcat~~

~~4.用undertow的httphandler适配接口，不能包含任何servlet接口~~

5.用netty适配接口，不能包含servlet和undertow的接口

~~6.写个example，能更改一个jar依赖就能顺利从tomcat迁移到undertow或netty~~

7.实现metric功能

~~8.实现简单的jdbc功能~~

~~9. 基于mvc内核实现httpclient功能~~

10.待续....
