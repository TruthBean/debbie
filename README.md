# debbie
for microservice

## 说明
    该项目使用Java11开发，为什么不适用kotlin，因为jetbrain开发的ktor框架已经很好用了啊（[滑稽]）；
    项目包管理采用gradle dsl。
    项目力求最简化，简单来说，就是能用自己写的尽量自己写；其次要模块界限分明，包引用简单化；
    使用spi而不是通过optional来引用其他包或项目，不会因为包引入给开发者带来困扰。

## construction

### package引用规范
尽量使用compileOnly，不要给开发者带来额外的包引用负担

### properties规范
properties名称一律小写，第三方框架的properties的key由驼峰换成“-”分割

@PropertyInject 标识的 field，通过setter方法来 注入的

## maven
[0.0.1-RELEASE package](./versions/0.0.1-RELEASE.md)

[0.0.2-RELEASE package](./versions/0.0.2-RELEASE.md)

## 目标
--------
~~1.先实现一个mvc内核~~

~~2.用servlet实现功能~~

~~3.集成一个tomcat~~

~~4.用undertow的httphandler适配接口，不能包含任何servlet接口~~

~~5.用netty适配接口，不能包含servlet和undertow的接口~~

~~6.写个example，能更改一个jar依赖就能顺利从tomcat迁移到undertow或netty~~

7.实现metric功能

~~8.实现简单的jdbc功能~~

~~9. 基于mvc内核实现httpclient功能~~

10.使用javassist或者ASM增强bean proxy功能

10.完善aio、rmi模块

11.待续....
