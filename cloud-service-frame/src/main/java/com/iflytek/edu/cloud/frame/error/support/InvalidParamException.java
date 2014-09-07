/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.error.support;

/**
 * 参数验证失败，抛出此异常
 * 
 * @author libinsong1204@gmail.com
 *
 */
@SuppressWarnings("serial")
public class InvalidParamException extends IllegalArgumentException {

	/**
	 * 参数名称
	 */
	private String paramName;
	/**
	 * 接收参数值
	 */
	private Object value;
	
	public InvalidParamException(String paramName, Object value, String message) {
		super(message);
		this.paramName = paramName;
		this.value = value;
    }

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
