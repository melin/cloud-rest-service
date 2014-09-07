/**
 * 
 */
package com.iflytek.edu.cloud.oauth2.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Create on @2014年8月5日 @下午1:38:28 
 * @author libinsong1204@gmail.com
 */
public class CORSFilter extends OncePerRequestFilter implements InitializingBean {
	private String origins;
	
	private Set<String> allowedOrigins;
	
	@Override
	protected void initFilterBean() throws ServletException {
		allowedOrigins = new HashSet<String>(Arrays.asList(origins.split(";")));
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String origin = request.getHeader("Origin");
        if(allowedOrigins.contains(origin)) {
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }
		
        filterChain.doFilter(request, response);
	}

	public String getOrigins() {
		return origins;
	}

	public void setOrigins(String origins) {
		this.origins = origins;
	}

}
