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
package com.iflytek.edu.cloud.frame.support.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 使用jedis shard连接到redis服务器。
 * 
 * Create on @2013-10-10 @上午10:21:20 
 * @author libinsong1204@gmail.com
 */
public class JedisPoolFactoryBean implements InitializingBean, DisposableBean, FactoryBean<JedisPool> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JedisPoolFactoryBean.class);
	
	private String redisAddress;
	
	private int timeout = 2000;
	
	private JedisPoolConfig config;
	
	private JedisPool jedisPool;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String[] arrs = redisAddress.split(":");
		jedisPool = new JedisPool(config, arrs[0], Integer.valueOf(arrs[1]));
	}
	
	@Override
	public void destroy() throws Exception {
		if(jedisPool != null) {
			try {
				jedisPool.destroy();
			} catch (Exception ex) {
				LOGGER.warn("Cannot properly close Jedis pool", ex);
			}
			jedisPool = null;
		}
	}
	
	@Override
	public JedisPool getObject() throws Exception {
		return jedisPool;
	}

	@Override
	public Class<?> getObjectType() {
		return JedisPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getRedisAddress() {
		return redisAddress;
	}

	public void setRedisAddress(String redisAddress) {
		this.redisAddress = redisAddress;
	}

	public JedisPoolConfig getConfig() {
		return config;
	}

	public void setConfig(JedisPoolConfig config) {
		this.config = config;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
