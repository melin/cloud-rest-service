/**
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
