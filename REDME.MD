# UseAge

## pom.xml import
```xml
        <dependency>
            <groupId>net.ifok.project</groupId>
            <artifactId>stateless-shiro-boot-starter</artifactId>
            <version>1.0.1-SNAPSHOT</version>
        </dependency>
```

## spring boot config

```properties
spring.shiro.stateless.url-patterns=/api/**
spring.shiro.stateless.token-name=token
```

## implement interface 
implement 
`net.ifok.project.stateless.shiro.service.StatelessSessionUserService`

## starter your's 
do things...