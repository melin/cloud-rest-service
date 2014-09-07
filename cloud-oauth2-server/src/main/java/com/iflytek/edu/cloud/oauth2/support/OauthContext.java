/**
 * 
 */
package com.iflytek.edu.cloud.oauth2.support;

import java.util.Map;

import jsr166e.ConcurrentHashMapV8;

/**
 * Create on @2014年8月5日 @上午10:08:40 
 * @author libinsong1204@gmail.com
 */
public class OauthContext {
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
