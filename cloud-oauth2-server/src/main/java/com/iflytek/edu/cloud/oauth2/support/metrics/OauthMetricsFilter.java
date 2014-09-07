/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.oauth2.support.metrics;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class OauthMetricsFilter extends GenericFilterBean  {
	public static final String SERVICE_EXEC_TIME = OauthMetricsFilter.class.getSimpleName() + "_exec_time";
	
	private OauthAppStat serviceAppStat;
	
	private JdbcTemplate jdbcTemplate;

	@Override
	public void initFilterBean() throws ServletException {
		if (serviceAppStat == null) {
			serviceAppStat = new OauthAppStat(jdbcTemplate);
        }
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        final String appkey = request.getParameter("client_id");
        final String grantType = request.getParameter("grant_type");
        
        if(StringUtils.isBlank(appkey) || StringUtils.isBlank(grantType)) {
        	chain.doFilter(request, response);
        	return;
        }
        
        long startNano = System.nanoTime();
        long startMillis = System.currentTimeMillis();
        
        serviceAppStat.beforeInvoke();

        RequestTokenStat requestToken = serviceAppStat.getRequestToken(appkey, grantType);
        
        if (requestToken != null) {
        	requestToken.beforeInvoke(startMillis);
        }
        
        Throwable error = null;
        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            error = e;
            throw e;
        } catch (ServletException e) {
            error = e;
            throw e;
        } catch (RuntimeException e) {
            error = e;
            throw e;
        } catch (Error e) {
            error = e;
            throw e;
        } finally {
            long endNano = System.nanoTime();
            long nanos = endNano - startNano;
            serviceAppStat.afterInvoke(error, nanos);

            if (requestToken != null) {
            	requestToken.afterInvoke(error, nanos);
            	request.setAttribute(SERVICE_EXEC_TIME, nanos / (1000 * 1000));
            }
        }
	}

	@Override
	public void destroy() {
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
