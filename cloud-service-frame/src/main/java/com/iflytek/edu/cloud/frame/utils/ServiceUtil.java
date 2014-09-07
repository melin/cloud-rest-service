/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public abstract class ServiceUtil {
	//通过前端的负载均衡服务器时，请求对象中的IP会变成负载均衡服务器的IP，因此需要特殊处理，下同。
    private static final String X_REAL_IP = "X-Real-IP";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    
	public static String getRemoteAddr(HttpServletRequest request) {  
	    String remoteIp = request.getHeader(X_REAL_IP); //nginx反向代理  
	    if (StringUtils.hasText(remoteIp)) {  
	        return remoteIp;  
	    } else {  
	        remoteIp = request.getHeader(X_FORWARDED_FOR);//apache反射代理  
	        if (StringUtils.hasText(remoteIp)) {  
	            String[] ips = remoteIp.split(",");  
	            for (String ip : ips) {  
	                if (!"null".equalsIgnoreCase(ip)) {  
	                    return ip;  
	                }  
	            }  
	        }  
	        return request.getRemoteAddr();  
	    }  
	}
	
}
