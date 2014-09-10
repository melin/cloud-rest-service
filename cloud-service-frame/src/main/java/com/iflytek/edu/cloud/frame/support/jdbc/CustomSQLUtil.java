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
package com.iflytek.edu.cloud.frame.support.jdbc;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author   libinsong1204@gmail.com
 * @Date	 2011-6-14 下午01:41:33
 */
public class CustomSQLUtil {
	private static Logger logger = LoggerFactory.getLogger(CustomSQLUtil.class);

	private final static CustomSQLUtil _instance = new CustomSQLUtil();
	
	private AtomicBoolean init = new AtomicBoolean(false);

	private CustomSQL _customSQL;
	
	private CustomSQLUtil() {
		try {
			if(init.compareAndSet(false, true))
				_customSQL = CustomSQL.getInstance();
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String get(String id) {
		return _instance._customSQL.get(id);
	}
	
	public static String get(String id, Map<String, Object> parameters) {
		return _instance._customSQL.get(id, parameters);
	}
}