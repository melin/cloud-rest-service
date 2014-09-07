/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.spring.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class ServiceMethodInfo implements RequestCondition<ServiceMethodInfo> {

	private final ServiceMethodRequestCondition serviceMethodCondition;

	private final RequestMethodsRequestCondition methodsCondition;

	/**
	 * Creates a new instance with the given request conditions.
	 */
	public ServiceMethodInfo(ServiceMethodRequestCondition patterns, RequestMethodsRequestCondition methods) {
		this.serviceMethodCondition = (patterns != null ? patterns : new ServiceMethodRequestCondition());
		this.methodsCondition = (methods != null ? methods : new RequestMethodsRequestCondition());
	}

	/**
	 * Re-create a RequestMappingInfo with the given custom request condition.
	 */
	public ServiceMethodInfo(ServiceMethodInfo info) {
		this(info.serviceMethodCondition, info.methodsCondition);
	}


	/**
	 * Returns the URL patterns of this {@link RequestMappingInfo};
	 * or instance with 0 patterns, never {@code null}.
	 */
	public ServiceMethodRequestCondition getServiceMethodCondition() {
		return this.serviceMethodCondition;
	}

	/**
	 * Returns the HTTP request methods of this {@link RequestMappingInfo};
	 * or instance with 0 request methods, never {@code null}.
	 */
	public RequestMethodsRequestCondition getMethodsCondition() {
		return this.methodsCondition;
	}

	/**
	 * Combines "this" request mapping info (i.e. the current instance) with another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * @return a new request mapping info instance; never {@code null}
	 */
	@Override
	public ServiceMethodInfo combine(ServiceMethodInfo other) {
		ServiceMethodRequestCondition patterns = this.serviceMethodCondition.combine(other.serviceMethodCondition);
		RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);

		return new ServiceMethodInfo(patterns, methods);
	}

	/**
	 * Checks if all conditions in this request mapping info match the provided request and returns
	 * a potentially new request mapping info with conditions tailored to the current request.
	 * <p>For example the returned instance may contain the subset of URL patterns that match to
	 * the current request, sorted with best matching patterns on top.
	 * @return a new instance in case all conditions match; or {@code null} otherwise
	 */
	@Override
	public ServiceMethodInfo getMatchingCondition(HttpServletRequest request) {
		RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);

		if (methods == null) {
			return null;
		}

		ServiceMethodRequestCondition patterns = this.serviceMethodCondition.getMatchingCondition(request);
		if (patterns == null) {
			return null;
		}

		return new ServiceMethodInfo(patterns, methods);
	}


	/**
	 * Compares "this" info (i.e. the current instance) with another info in the context of a request.
	 * <p>Note: It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} to ensure they have conditions with
	 * content relevant to current request.
	 */
	@Override
	public int compareTo(ServiceMethodInfo other, HttpServletRequest request) {
		int result = this.serviceMethodCondition.compareTo(other.getServiceMethodCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
		if (result != 0) {
			return result;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof ServiceMethodInfo) {
			ServiceMethodInfo other = (ServiceMethodInfo) obj;
			return (this.serviceMethodCondition.equals(other.serviceMethodCondition) &&
					this.methodsCondition.equals(other.methodsCondition));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.serviceMethodCondition.hashCode() * 31 +  // primary differentiation
				this.methodsCondition.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		builder.append(this.serviceMethodCondition);
		builder.append(",methods=").append(this.methodsCondition);
		builder.append('}');
		return builder.toString();
	}

}
