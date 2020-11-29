# Stateless Apache Shiro Stater

## 简介
`net.ifok.shiro:stateless-shiro-boot-starter`项目是整合Apache Shiro 无状态化鉴权成为spring boot的一个starter项目，使用方便。
简单来说就是禁用了session相关鉴权方式，通过token的方式来进行鉴权，适合前后端分离项目，配合`net.ifok.swagger:swagger-boot-starter`能快速搭建出一个既有鉴权又有Api doc的后台服务。
## pom.xml导入maven依赖
```xml
        <dependency>
            <groupId>net.ifok.shiro</groupId>
            <artifactId>stateless-shiro-boot-starter</artifactId>
            <version>1.1.1</version>
        </dependency>
```
## 实现下面接口
implement 
`net.ifok.project.stateless.shiro.service.StatelessSessionUserService`
> 说明：上方接口用于鉴权的token获取会话对象和创建token等操作用。
## Spring Boot 项目application.properties文件配置

```properties
# 那些url需要鉴权，默认/** 所有
spring.shiro.stateless.url-patterns=/api/**
# 排除那些不需要拦截的
spring.shiro.stateless.url-excludes=/api/auth/login,/api/auth/logout
# token 名称 默认accessToken，默认从http协议的header获取，如果获取不到则通过参数获取。
spring.shiro.stateless.token-name=token
```

## 依赖说明
- spring-boot-starter-web 2.2.6.RELEASE
- shiro-spring 1.5.3
- fastjson 1.2.73

## 许可说明
Apache Software License, Version 2.0