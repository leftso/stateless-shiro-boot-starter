package net.ifok.project.stateless.shiro.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.ifok.project.stateless.shiro.filter.StatelessAccessControlFilter;
import net.ifok.project.stateless.shiro.model.StatelessShiroProperties;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/***
 * 注意，无状态会话，shiro本身不需要缓存，缓存用来控制token过期
 * @author xqlee
 *
 */
@Slf4j
@Configuration
public class StatelessShiroConfig {

	static final String DEFAULT_URL="/**";
	static final String ANON="anon";
	static final String STATELESS_FILTER_NAME="statelessAuthc";
	static final String STATELESS_FILTER_FACTORY_NAME="shiroFilterFactoryBean";

	@Autowired
	StatelessShiroProperties statelessShiroProperties;

	/****
	 * 注入无状态的realm
	 * 
	 * @return
	 */
	@Bean
	public StatelessRealm userRealm() {
		StatelessRealm realm = new StatelessRealm();
		//设置禁用缓存（因为缓存不适用无状态）
		realm.setCachingEnabled(false);
		return realm;
	}

	/**
	 * 自定义的无状态（不创建session）Subject工厂
	 * 
	 * @return
	 */
	@Bean
	public StatelessDefaultSubjectFactory subjectFactory() {
		return new StatelessDefaultSubjectFactory();
	}

	/**
	 * sessionManager通过sessionValidationSchedulerEnabled禁用掉会话调度器，
	 * 因为我们禁用掉了会话，所以没必要再定期过期会话了。
	 * 
	 * @return
	 */
	@Bean
	public SessionManager sessionManager() {
		DefaultSessionManager sessionManager = new DefaultSessionManager();
		//关闭Session验证（无状态认证不需要session认证）
		sessionManager.setSessionValidationSchedulerEnabled(false);
		return sessionManager;
	}
	/**
	 * 注入SessionStorageEvaluator,关闭Session存储
	 */
	@Bean
	public SessionStorageEvaluator sessionStorageEvaluator() {
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		return defaultSessionStorageEvaluator;
	}

	/***
	 * 安全管理配置
	 * 
	 * @return
	 */
	@Bean
	public SecurityManager securityManager(
			StatelessRealm statelessRealm, SessionStorageEvaluator sessionStorageEvaluator,
			SubjectFactory subjectFactory, SessionManager sessionManager) {
		DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();


		defaultSecurityManager.setRealm(statelessRealm);

		DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
		defaultSubjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
		defaultSecurityManager.setSubjectDAO(defaultSubjectDAO);

		defaultSecurityManager.setSubjectFactory(subjectFactory);

		defaultSecurityManager.setSessionManager(sessionManager);

		return defaultSecurityManager;
	}


	/**
	 * Add. 访问控制器.
	 *
	 * @return
	 */
	@Bean
	public StatelessAccessControlFilter statelessAuthcFilter() {
		StatelessAccessControlFilter statelessAuthcFilter = new StatelessAccessControlFilter();
		return statelessAuthcFilter;
	}

	/**
	 * 拦截器配置
	 */
	@Bean(name = STATELESS_FILTER_FACTORY_NAME)
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, StatelessAccessControlFilter statelessAccessControlFilter) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		//添加自定义的拦截器
		Map<String, Filter> filters = new HashMap<>();
		filters.put(STATELESS_FILTER_NAME, statelessAccessControlFilter);
		shiroFilterFactoryBean.setFilters(filters);


		//设置自定义的拦截器,拦截所有请求,方法一
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap();
		//获取排除权限及登录认证的路径
		List<String> urlExcludes = statelessShiroProperties.getUrlExcludes();
		if (!CollectionUtils.isEmpty(urlExcludes)){
			log.info("Shiro URL Excludes : {}", JSON.toJSONString(urlExcludes));
			for (String urlExclude : urlExcludes) {
				filterChainDefinitionMap.put(urlExclude,ANON);
			}
		}else {
			log.info("Shiro URL Excludes Is Empty .");
		}
		/**
		 * 标识 /api/**路径走statelessAuthc 过滤认证
		 */
		List<String> urlPatterns = statelessShiroProperties.getUrlPatterns();
		if (CollectionUtils.isEmpty(urlPatterns)){
			log.warn("Shiro urlPatterns is empty,set default /**");
			filterChainDefinitionMap.put(DEFAULT_URL, STATELESS_FILTER_NAME);
		}else {
			for (String urlPattern : urlPatterns) {
				filterChainDefinitionMap.put(urlPattern, STATELESS_FILTER_NAME);
			}
		}
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 拦截器注册
	 */
	@Bean
	public FilterRegistrationBean delegatingFilterProxy() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		DelegatingFilterProxy proxy = new DelegatingFilterProxy();
		proxy.setTargetFilterLifecycle(true);
		proxy.setTargetBeanName(STATELESS_FILTER_FACTORY_NAME);
		filterRegistrationBean.setFilter(proxy);
		return filterRegistrationBean;
	}

	/**
	 * *
	 * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),
	 * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
	 * *
	 * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)
	 * 和AuthorizationAttributeSourceAdvisor)即可实现此功能
	 * *
	 */
	@Bean
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}
}
