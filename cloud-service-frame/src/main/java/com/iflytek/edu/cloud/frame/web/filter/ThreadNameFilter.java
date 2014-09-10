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
import javax.servlet.http.HttpServletRequest;

import com.iflytek.edu.cloud.frame.Constants;

/**
 * 修改请求线程名称 
 * 
 * @author libinsong1204@gmail.com
 */
public class ThreadNameFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }

    @Override
    public void destroy() { /* unused */ }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final Thread current = Thread.currentThread();
        final String oldName = current.getName();
        try {
            current.setName(formatName(req, oldName));
            chain.doFilter(request, response);
        } finally {
            current.setName(oldName);
        }
    }

    private static String formatName(HttpServletRequest req, String oldName) {
    	final String method = req.getParameter(Constants.SYS_PARAM_KEY_METHOD);
        return oldName + "-" + req.getMethod() + "-" + method;
    }
}
