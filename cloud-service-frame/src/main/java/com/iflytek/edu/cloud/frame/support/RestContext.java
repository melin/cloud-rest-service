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
package com.iflytek.edu.cloud.frame.support;

import java.util.Map;

import jsr166e.ConcurrentHashMapV8;

/**
 * Create on @2014年8月5日 @上午10:08:40 
 * @author libinsong1204@gmail.com
 */
public class RestContext {
	private Map<String, Object> params = new ConcurrentHashMapV8<String, Object>();
	
	private int callCycoreCount;
	private long callCycoreTime;

	public Object getParam(String key) {
		return params.get(key);
	}

	public void addParam(String key, Object value) {
		params.put(key, value);
	}

	public int getCallCycoreCount() {
		return callCycoreCount;
	}

	public void incrCallCycoreCount() {
		this.callCycoreCount++;
	}

	public long getCallCycoreTime() {
		return callCycoreTime;
	}

	public void addCallCycoreTime(long execTime) {
		this.callCycoreTime = this.callCycoreTime + execTime;
	}
}
