package net.ifok.project.stateless.shiro;

import net.ifok.project.stateless.shiro.config.StatelessShiroConfig;
import net.ifok.project.stateless.shiro.model.StatelessShiroProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description:  自动加载
 * @Author: xq 
 * @Date: 2020/11/26 13:29
 **/
@Configuration
@Import(value = {StatelessShiroProperties.class, StatelessShiroConfig.class})
public class AutoConfig {
}
