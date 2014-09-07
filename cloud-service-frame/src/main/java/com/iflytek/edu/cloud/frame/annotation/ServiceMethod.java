/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iflytek.edu.cloud.frame.support.GrantType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ServiceMethod {
	
	String value() default "";
	
	String version() default "";
	
	GrantType[] grantType() default {GrantType.AUTH_CODE, GrantType.IMPLICIT, 
		GrantType.REFRESH_TOKEN, GrantType.CLIENT, GrantType.PASSWORD};
	
	RequestMethod[] method() default {RequestMethod.GET, RequestMethod.POST};
}
