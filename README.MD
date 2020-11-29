# 使用说明

## pom.xml导入maven依赖
```xml
        <dependency>
            <groupId>net.ifok.shiro</groupId>
            <artifactId>stateless-shiro-boot-starter</artifactId>
            <version>1.1.0</version>
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
# token 名称 默认accessToken，默认从http协议的header获取，如果获取不到则通过参数获取。
spring.shiro.stateless.token-name=token
```

## 依赖说明
- spring-boot-starter-web 2.2.6.RELEASE
- shiro-spring 1.5.3
- fastjson 1.2.73

## 许可说明
Apache Software License, Version 2.0