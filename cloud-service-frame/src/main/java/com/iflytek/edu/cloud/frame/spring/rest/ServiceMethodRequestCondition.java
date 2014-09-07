/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
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

