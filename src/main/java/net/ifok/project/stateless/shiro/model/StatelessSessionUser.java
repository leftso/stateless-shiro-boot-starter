package net.ifok.project.stateless.shiro.model;

import lombok.Data;

import java.util.List;

/**
 * @Description:  无状态会话用户信息，实际使用可以集成该类扩展更多属性
 * @Author: xq 
 * @Date: 2020/11/26 14:09
 **/
@Data
public class StatelessSessionUser {
    /**
     * token值
     */
    String accessToken;
    /**
     * 用户权限
     */
    List<String> permissions;
    /**
     * 用户角色
     */
    List<String> roles;
}
