/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.spring;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.iflytek.edu.cloud.frame.utils.EnvUtil;

/**
 * @author libinsong1204@gmail.com
 * @date 2012-3-1 下午1:20:50
 */
public class ProfileApplicationContextInitializer implements
		ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger _logger = LoggerFactory.getLogger(ProfileApplicationContextInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String[] profiles = EnvUtil.getSpringProfiles();
		applicationContext.getEnvironment().setActiveProfiles(profiles);
		_logger.info("Active spring profile: {}", StringUtils.join(profiles, ","));
	}

}
