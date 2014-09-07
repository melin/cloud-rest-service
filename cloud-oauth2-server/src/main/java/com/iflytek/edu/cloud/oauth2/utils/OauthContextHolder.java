/**
 * 
 */
package com.iflytek.edu.cloud.oauth2.utils;

import com.iflytek.edu.cloud.oauth2.support.OauthContext;

/**
 * Create on @2014年8月5日 @上午10:08:58 
 * @author libinsong1204@gmail.com
 */
public class OauthContextHolder {
	private static final ThreadLocal<OauthContext> contextHolder = new ThreadLocal<OauthContext>();
	
	public static void clearContext() {
		contextHolder.remove();
    }

    public static OauthContext getContext() {
    	OauthContext ctx = contextHolder.get();
        
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public static void setContext(OauthContext context) {
    	contextHolder.set(context);
    }
    
    private static OauthContext createEmptyContext() {
        return new OauthContext();
    }
}
