package net.ifok.project.stateless.shiro.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 无状态 shiro 配置
 * @Author: xq
 * @Date: 2020/11/26 13:33
 **/
@Component
@ConfigurationProperties(prefix = "spring.shiro.stateless")
@Data
public class StatelessShiroProperties {
    /**
     * 需要拦截的url地址，默认拦截所有 /**
     */
    List<String> urlPatterns= Arrays.asList("/**");
    /**
     * 需要排除那些url地址，默认无
     */
    List<String> urlExcludes;
    /**
     * token名称（通过参数或者header获取，优先取header里面的）
     */
    String tokenName="accessToken";


}
