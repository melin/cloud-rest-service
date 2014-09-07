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
