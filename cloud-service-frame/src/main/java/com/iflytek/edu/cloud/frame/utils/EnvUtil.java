/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.edu.cloud.frame.Constants;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public abstract class EnvUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvUtil.class);
	
	private static String[] profiles;
	private static String profile;
	private static String projectName;
	private static String projectBaseDir;
	private static String buildVersion;
	private static String buildTime;
	
	static {
		try {
			Configuration config = new PropertiesConfiguration("META-INF/res/env.properties");
			
			LOGGER.info("加载env.properties");
			
			profile = getProfile();
			profiles = config.getStringArray("spring.profiles.active");
			if(StringUtils.isBlank(profiles[0]))
				profiles[0] = profile;
			else
				profiles = (String[]) ArrayUtils.add(profiles, 0, profile);
			
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
		return profile.equals(Constants.PROFILE_DEVELOPMENT);
	}
	
	public static boolean isTest() {
		return profile.equals(Constants.PROFILE_TEST);
	}
	
	public static boolean isBuild() {
		return profile.equals(Constants.PROFILE_BUILD);
	}
	
	public static boolean isProduction() {
		return profile.equals(Constants.PROFILE_PRODUCTION);
	}
	
	public static boolean oauthEnabled() {
		if(profiles != null && existProfile(profiles, Constants.PROFILE_OAUTH))
			return true;
		else
			return false;
	}
	
	public static boolean jdbcEnabled() {
		if(profiles != null && existProfile(profiles, Constants.PROFILE_JDBC))
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
	
	public static String getProfile() {
		if(profile != null)
			return profile;
		
		profile = System.getenv("SUPERDIAMOND_PROFILE");
		if(StringUtils.isBlank(profile)) {
			return System.getProperty("superdiamond.profile", "development").trim();
		} else {
			return profile.trim();
		}
	}
}
