/**
 * 
 */
package com.iflytek.edu.cloud.frame.spring;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import redis.clients.jedis.JedisPool;

import com.iflytek.edu.cloud.frame.utils.EnvUtil;

/**
 * Create on @2014年8月1日 @下午8:13:17 
 * @author libinsong1204@gmail.com
 */
public class TokenStoreFactoryBean implements FactoryBean<TokenStore>, InitializingBean, ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenStoreFactoryBean.class);
	
	private ApplicationContext applicationContext;
	
	private TokenStore tokenStore;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(EnvUtil.redisEnabled()) {
			JedisPool jedisPool = applicationContext.getBean(JedisPool.class);
			tokenStore = new RedisTokenStore(jedisPool);
			LOGGER.info("使用redis存储token");
		} else {
			DataSource dataSource = applicationContext.getBean(DataSource.class);
			tokenStore = new JdbcTokenStore(dataSource);
			LOGGER.info("使用jdbc存储token");
		}
	}
	
	@Override
	public TokenStore getObject() throws Exception {
		return tokenStore;
	}

	@Override
	public Class<?> getObjectType() {
		return TokenStore.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
