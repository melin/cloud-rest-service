/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.error.support;

/**
 * 如果缺少参数，抛出此异常
 * 
 * @author libinsong1204@gmail.com
 *
 */
@SuppressWarnings("serial")
public class MissingParamException extends IllegalArgumentException {

	private String paramName;
	
	public MissingParamException(String paramName) {
		this(paramName, "缺少参数 " + paramName);
    }
	
	public MissingParamException(String paramName, String message) {
		super(message);
		this.paramName = paramName;
    }

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
