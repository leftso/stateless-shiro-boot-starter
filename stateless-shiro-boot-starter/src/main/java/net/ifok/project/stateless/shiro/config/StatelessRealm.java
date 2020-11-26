package net.ifok.project.stateless.shiro.config;

import net.ifok.project.stateless.shiro.model.StatelessSessionUser;
import net.ifok.project.stateless.shiro.service.StatelessSessionUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 实现一个基于JDBC的Realm,继承AuthorizingRealm可以看见需要重写两个方法,doGetAuthorizationInfo和doGetAuthenticationInfo
 *
 * @author xqlee
 */
@Component
public class StatelessRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(StatelessRealm.class);

    @Autowired
    StatelessSessionUserService statelessSessionUserService;

    /**
     * 启用token支持
     *
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    /***
     * 获取用户授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("##################执行Shiro权限认证##################");
        // 获取用户名
        StatelessSessionUser account = (StatelessSessionUser) principalCollection.getPrimaryPrincipal();
        // 判断用户名是否存在
        if (StringUtils.isEmpty(account)) {
           throw new RuntimeException("获取用户授权信息失败");
        }
        // 创建一个授权对象
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 进行权限设置
        List<String> permissions = account.getPermissions();
        if (permissions != null && !permissions.isEmpty()) {
            info.addStringPermissions(permissions);
        }
        // 角色设置
        List<String> roles = account.getRoles();
        if (roles != null) {
            info.addRoles(roles);
        }
        return info;
    }

    /**
     * 获取用户认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        logger.info("##################执行Shiro登陆认证##################");
        // 通过表单接收的用户名
        String token = (String)authenticationToken.getPrincipal();
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("token无效");
        }
        // 根据token获取用户信息
        StatelessSessionUser sessionUser = statelessSessionUserService.getStatelessSessionUser(token);

        if (Objects.isNull(sessionUser)) {
            throw new AuthenticationException("token无效");
        }
        return new SimpleAuthenticationInfo(sessionUser, authenticationToken.getCredentials(), getName());
    }

}
