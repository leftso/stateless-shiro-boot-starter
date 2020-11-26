package net.ifok.project.stateless.shiro.service;

import net.ifok.project.stateless.shiro.model.StatelessSessionUser;

/**
 * @Description:  会话内容存放或查询
 * @Author: xq 
 * @Date: 2020/11/26 14:13
 **/
public interface StatelessSessionUserService {
    /**
     * 通过令牌获取会话用户信息
     * @param accessToken 登录令牌
     * @return 用户会话对象
     */
    StatelessSessionUser getStatelessSessionUser(String accessToken);

    /**
     * 创建accessToken，注意创建后放入缓存，并设置过期时间
     * @param statelessSessionUser
     * @return
     */
    String createAccessToken(StatelessSessionUser statelessSessionUser);

    /**
     * 删除缓存的会话信息
     *
     * @param accessToken
     */
    void logout(String accessToken);

}
