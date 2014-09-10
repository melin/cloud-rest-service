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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.iflytek.edu.cloud.frame.jetty.JettyConfigUtil;
import com.iflytek.edu.cloud.frame.support.NamedThreadFactory;
import com.iflytek.edu.cloud.frame.utils.EnvUtil;
import com.iflytek.edu.cloud.frame.utils.SystemPropertyUtil;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class RedisRegistry implements InitializingBean{
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisRegistry.class);
	
	public static final int DEFAULT_SESSION_TIMEOUT = 60 * 1000;
	
	private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("CloudServiceRegistryExpireTimer", true));
	
	private JedisPool jedisPool;
	
	private int expirePeriod = DEFAULT_SESSION_TIMEOUT;
	
	private final ScheduledFuture<?> expireFuture;
	
	private final static String REGISTER_HASH_KEY = "register_hash_key";
	
	private final String redisValue;
	
	public RedisRegistry() {
		String baseDir = SystemPropertyUtil.get("BASE_HOME");
		redisValue = baseDir + "$$" + JettyConfigUtil.getServerPort();

        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    deferExpired(); // 延长过期时间
                } catch (Throwable t) { // 防御性容错
                	LOGGER.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
                }
            }
        }, expirePeriod, expirePeriod, TimeUnit.MILLISECONDS);
        
        LOGGER.info("服务器注册：" + EnvUtil.getProjectName());
    }
	
	private void deferExpired() {
		Jedis jedis = jedisPool.getResource();
        try {
        	jedis.hset(REGISTER_HASH_KEY, redisValue, String.valueOf(System.currentTimeMillis() + expirePeriod));
        } catch (Throwable t) {
        	LOGGER.warn("Failed to write provider heartbeat to redis registry. cause: " + t.getMessage(), t);
        } finally {
        	jedisPool.returnResource(jedis);
        }
    }
	
	public void afterPropertiesSet() throws java.lang.Exception {
        String expire = String.valueOf(System.currentTimeMillis() + expirePeriod);
        boolean success = false;
        RuntimeException exception = null;
        
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(REGISTER_HASH_KEY, redisValue, expire);
            success = true;
        } catch (Throwable t) {
            exception = new RuntimeException("Failed to register service to redis registry. registry", t);
        } finally {
        	jedisPool.returnResource(jedis);
        }
        if (exception != null) {
            if (success) {
            	LOGGER.warn(exception.getMessage(), exception);
            } else {
                throw exception;
            }
        }
	}
	
    public void destroy() {
        try {
            expireFuture.cancel(true);
        } catch (Throwable t) {
        	LOGGER.warn(t.getMessage(), t);
        }
    }

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
    
}
