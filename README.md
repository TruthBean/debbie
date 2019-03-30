# debbie
for microservice

## 目标
--------
1.先实现一个mvc内核

2.用servlet实现功能

3.集成一个tomcat

4.用undertow的httphandler适配接口，不能包含任何servlet接口

5.用netty适配接口，不能包含servlet和undertow的接口

6.写个example，能更改一个jar依赖就能顺利从tomcat迁移到undertow或netty

7.实现metric功能

8.实现简单的jdbc功能

9.集成CDI、spring bean

10.待续....

## 待定项
---------
~~1.是否用kotlin~~

不用kotlin，如果使用kotlin，建议使用jetbrain开发的ktor框架

~~2.用java8还是java11~~

用最新的版本

~~3.用gradle还是maven~~

使用gradle kotlin dsl
