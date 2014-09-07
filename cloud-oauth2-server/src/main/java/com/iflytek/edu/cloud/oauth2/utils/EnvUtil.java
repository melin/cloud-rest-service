/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.oauth2.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.edu.cloud.oauth2.Constants;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public abstract class EnvUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvUtil.class);
	
	private static String[] profiles;
	private static String projectName;
	private static String projectBaseDir;
	private static String buildVersion;
	private static String buildTime;
	
	static {
		try {
			Configuration config = new PropertiesConfiguration("META-INF/res/env.properties");
			
			LOGGER.info("加载env.properties");
			
			profiles = config.getStringArray("spring.profiles.active");
			projectName = config.getString("project.name");
			projectBaseDir = config.getString("project.basedir");
			buildVersion = config.getString("build.version");
			buildTime = config.getString("build.time");
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static String[] getSpringProfiles() {
		return profiles;
	}
	
	public static boolean isDevelopment() {
		if(profiles != null && !existProfile(profiles, Constants.PROFILE_TEST) && 
				!existProfile(profiles, Constants.PROFILE_PRODUCTION))
			return true;
		else
			return false;
	}
	
	public static boolean isTest() {
		if(profiles != null && existProfile(profiles, Constants.PROFILE_TEST))
			return true;
		else
			return false;
	}
	
	public static boolean isProduction() {
		if(profiles != null && existProfile(profiles, Constants.PROFILE_PRODUCTION))
			return true;
		else
			return false;
	}

	public static boolean redisEnabled() {
		if(profiles != null && existProfile(profiles, Constants.PROFILE_REDIS))
			return true;
		else
			return false;
	}
	
	private static boolean existProfile(String[] profiles, String check) {
		for(String profile : profiles) {
			if(profile.equals(check)) {
				return true;
			}
		}
		
		return false;
	}

	public static String getProjectName() {
		return projectName;
	}

	public static String getProjectBaseDir() {
		return projectBaseDir;
	}

	public static String getBuildVersion() {
		return buildVersion;
	}

	public static String getBuildTime() {
		return buildTime;
	}
	
}
