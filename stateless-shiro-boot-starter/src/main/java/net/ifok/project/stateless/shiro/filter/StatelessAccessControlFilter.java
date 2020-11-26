package net.ifok.project.stateless.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import net.ifok.project.stateless.shiro.model.StatelessShiroProperties;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description:  该过滤器需在shiro配置类中加入filter链
 * @Author: xq
 * @Date: 2020/11/26 12:28
 **/
@Slf4j
public class StatelessAccessControlFilter extends AccessControlFilter {

    @Autowired
    StatelessShiroProperties statelessShiroProperties;

    /**
     * 先执行：isAccessAllowed 再执行onAccessDenied
     * <p>
     * isAccessAllowed：表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，
     * 如果允许访问返回true，否则false；
     * <p>
     * 如果返回true的话，就直接返回交给下一个filter进行处理。 如果返回false的话，回往下执行onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        return false;
    }

    /**
     * onAccessDenied：表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；
     * 如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //==============================该步骤主要是通过token代理登录shiro======================
        HttpServletRequest req = (HttpServletRequest) request;
        //获取参数中的token值
        String token = req.getHeader(statelessShiroProperties.getTokenName());
        if (StringUtils.isEmpty(token)){
            token = request.getParameter(statelessShiroProperties.getTokenName());
        }
        //生成无状态Token然后代理登录,将token传入AuthenticationToken
        String finalToken = token;
        AuthenticationToken authenticationToken = new AuthenticationToken() {
            @Override
            public Object getPrincipal() {
                return finalToken;
            }

            @Override
            public Object getCredentials() {
                return finalToken;
            }
        };
        try {
            // 委托给Realm进行登录
            getSubject(request, response).login(authenticationToken);
        } catch (UnknownAccountException ue) {
            log.debug(ue.getLocalizedMessage());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            //登录失败不用处理后面的过滤器会处理并且能通过@ControllerAdvice统一处理相关异常
        }
        return true;
    }
}
