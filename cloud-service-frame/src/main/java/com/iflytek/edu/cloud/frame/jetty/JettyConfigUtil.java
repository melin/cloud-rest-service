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
package com.iflytek.edu.cloud.frame.jetty;

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
