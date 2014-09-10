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
package com.iflytek.edu.cloud.frame.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 限制每秒Http 请求数量，默认值为：300
 * 
 * Create on @2013-8-19 @下午7:13:01 
 * @author libinsong1204@gmail.com
 */
public class HttpRequestRateLimiterFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestRateLimiterFilter.class);
	private static final int DEFAULT_PERMITS_PER_SECOND = 300;
	private RateLimiter limiter;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String value = filterConfig.getInitParameter("permitsPerSecond");
		int permitsPerSecond = DEFAULT_PERMITS_PER_SECOND;
		if(StringUtils.hasText(value))
			permitsPerSecond = Integer.valueOf(value);
		
		limiter = RateLimiter.create(permitsPerSecond);
		
		LOGGER.info("限制http请求数为：{}", permitsPerSecond);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(limiter.tryAcquire()) {
			chain.doFilter(request, response);
		} else {
			((HttpServletResponse)response).sendError(429, "http请求数太多");
		}
	}

	@Override
	public void destroy() {
	}

}
