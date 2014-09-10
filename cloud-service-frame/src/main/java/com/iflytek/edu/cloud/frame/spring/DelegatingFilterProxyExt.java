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
package com.iflytek.edu.cloud.frame.spring;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * 判断指定appkey不需要认证授权
 * 
 * Create on @2014年8月7日 @下午7:47:34 
 * @author libinsong1204@gmail.com
 */
public class DelegatingFilterProxyExt extends DelegatingFilterProxy {
	private static final Logger logger = LoggerFactory.getLogger(DelegatingFilterProxyExt.class);
	
	private List<String> excludeAppkeys;
	
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		String appkey = System.getProperty("excludes.appkey");
		if(StringUtils.hasText(appkey)) {
			excludeAppkeys = Arrays.asList(appkey.split(","));
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String appkey = request.getParameter("appkey");
		if(excludeAppkeys != null && excludeAppkeys.contains(appkey)) {
			logger.debug("应用(appkey=" + appkey + ")不需要oauth安全认证。");
			filterChain.doFilter(request, response);
		} else
			super.doFilter(request, response, filterChain);
	}
}
