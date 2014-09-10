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
package com.iflytek.edu.cloud.frame.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

import com.iflytek.edu.cloud.frame.utils.EnvUtil;

/**
 * Create on @2014年8月24日 @下午2:53:09
 * 
 * @author libinsong1204@gmail.com
 */
public class LogBackLoadConfigureListener implements ServletContextListener {
	final static Logger logger = LoggerFactory
			.getLogger(LogBackLoadConfigureListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			ClassPathResource resource = new ClassPathResource("logback-default.xml");
			if(!resource.exists()) {
				String profile = EnvUtil.getProfile();
				resource = new ClassPathResource("META-INF/logback/logback-" + profile + ".xml");
			}
			
			configurator.doConfigure(resource.getInputStream());
			logger.info("加载logback配置文件：" + resource.getURL().getPath());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
