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
package com.iflytek.edu.cloud.frame.spring.rest;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import com.iflytek.edu.cloud.frame.Constants;

public class ServiceMethodRequestCondition extends AbstractRequestCondition<ServiceMethodRequestCondition> {

	private String method;
	
	private String version;
	
	public ServiceMethodRequestCondition() {
	}

	public ServiceMethodRequestCondition(String method, String version) {
		this.version = version;
		this.method = method;
	}

	public String getMethod() {
		return this.method;
	}
	
	public String getVersion() {
		return version;
	}

	@Override
	protected Collection<String> getContent() {
		return Arrays.asList(method);
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	@Override
	public ServiceMethodRequestCondition combine(ServiceMethodRequestCondition other) {
		return new ServiceMethodRequestCondition(other.getMethod(), other.getVersion());
	}

	@Override
	public ServiceMethodRequestCondition getMatchingCondition(HttpServletRequest request) {
		String _method = request.getParameter(Constants.SYS_PARAM_KEY_METHOD);
		String _version = request.getParameter(Constants.SYS_PARAM_KEY_VERSION);
		return new ServiceMethodRequestCondition(_method, _version);
	}

	@Override
	public int compareTo(ServiceMethodRequestCondition other, HttpServletRequest request) {
		return 0;
	}

}

