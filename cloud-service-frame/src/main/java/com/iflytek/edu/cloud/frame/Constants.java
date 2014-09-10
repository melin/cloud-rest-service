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
package com.iflytek.edu.cloud.frame;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public abstract class Constants {
	
	/**
	 * 系统级参数key。
	 */
	public static final String SYS_PARAM_KEY_METHOD = "method";
	
	public static final String SYS_PARAM_KEY_FORMAT = "format";
	
	public static final String SYS_PARAM_KEY_VERSION = "version";
	
	public static final String SYS_PARAM_KEY_LOCALE = "locale";
	
	public static final String SYS_PARAM_KEY_APPKEY = "appkey";
	
	public static final String SYS_PARAM_KEY_ACCESS_TOKEN = "access_token";
	
	public static final String SYS_PARAM_KEY_CALLBACK = "callback";
	
	public static final String MAIN_ERROR_CODE = "MAIN_ERROR_CODE";
	
	/**
	 * 服务框架只支持两个http method：get & post
	 */
	public static final String HTTP_METHOD_GET = "GET";
	
	public static final String HTTP_METHOD_POST = "POST";
	
	/**
	 * 返回数据格式支持：xml & json
	 */
	public static final String DATA_FORMAT_XML = "xml";
	
	public static final String DATA_FORMAT_JSON = "json";
	
	/**
	 * 系统profile：development、test、production
	 */
	public static final String PROFILE_DEVELOPMENT = "development";
	
	public static final String PROFILE_TEST = "test";
	
	public static final String PROFILE_BUILD = "build";
	
	public static final String PROFILE_PRODUCTION = "production";
	
	public static final String PROFILE_OAUTH = "oauth";
	
	public static final String PROFILE_JDBC = "jdbc";
	
	public static final String PROFILE_REDIS = "redis";
}
