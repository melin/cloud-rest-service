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
package com.iflytek.edu.cloud.frame.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.iflytek.edu.cloud.frame.support.RestContext;

/**
 * Create on @2014年8月5日 @上午10:08:58 
 * @author libinsong1204@gmail.com
 */
public class RestContextHolder {
	private static final ThreadLocal<RestContext> contextHolder = new ThreadLocal<RestContext>();
	
	public static void clearContext() {
		contextHolder.remove();
    }

    public static RestContext getContext() {
    	RestContext ctx = contextHolder.get();
        
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public static void setContext(RestContext context) {
    	contextHolder.set(context);
    }
    
    /**
     * 当使用password 授权类型时，获取用户名
     * 
     * @return
     */
    public static String getUsername() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if(authentication != null) {
    		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
    		if(oAuth2Authentication.getUserAuthentication() != null)
    			return oAuth2Authentication.getUserAuthentication().getName();
    	} 
    	
    	return null;
    }
    
    private static RestContext createEmptyContext() {
        return new RestContext();
    }
}
