/**
 * Copyright (c) 2012,USTC E-BUSINESS TECHNOLOGY CO.LTD All Rights Reserved.
 */

package com.iflytek.edu.cloud.oauth2.support;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.iflytek.edu.cloud.oauth2.utils.EnvUtil;

/**
 * @author bsli@starit.com.cn
 * @date 2012-3-1 下午1:20:50
 */
public class ProfileApplicationContextInitializer implements
		ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger _logger = LoggerFactory.getLogger(ProfileApplicationContextInitializer.class);

	public static String profile;
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String[] profiles = EnvUtil.getSpringProfiles();
		applicationContext.getEnvironment().setActiveProfiles(profiles);
		_logger.info("Active spring profile: {}", StringUtils.join(profiles, ","));
	}

}
