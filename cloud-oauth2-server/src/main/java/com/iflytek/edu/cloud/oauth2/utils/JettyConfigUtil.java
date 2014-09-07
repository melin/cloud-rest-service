/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.oauth2.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create on @2014年7月25日 @下午12:46:39 
 * @author libinsong1204@gmail.com
 */
public class JettyConfigUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JettyConfigUtil.class);
	
	private static int maxThreads;
	private static int minThreads;
	private static int serverPort;
	
	static {
		try {
			org.apache.commons.configuration.Configuration config = 
					new PropertiesConfiguration("META-INF/res/jetty.properties");
			
			LOGGER.info("加载jetty.properties");
			
			maxThreads = config.getInt("thread.pool.max.size", 100);
			minThreads = config.getInt("thread.pool.min.size", 10);
			serverPort = config.getInt("server.port", 8080);
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public static int getMaxThreads() {
		return maxThreads;
	}

	public static int getMinThreads() {
		return minThreads;
	}

	public static int getServerPort() {
		return serverPort;
	}
}
