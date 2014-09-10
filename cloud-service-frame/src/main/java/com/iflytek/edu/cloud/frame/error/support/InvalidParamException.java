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
