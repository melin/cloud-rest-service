/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
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
