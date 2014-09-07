/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.spring;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

import com.iflytek.edu.cloud.frame.error.MainError;
import com.iflytek.edu.cloud.frame.error.MainErrorType;
import com.iflytek.edu.cloud.frame.error.MainErrors;
import com.iflytek.edu.cloud.frame.error.support.ErrorRequestMessageConverter;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class OAuth2AuthenticationEntryPointExt extends OAuth2AuthenticationEntryPoint {
	private ErrorRequestMessageConverter messageConverter;

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		if(authException.getCause() instanceof InvalidTokenException) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			MainError mainError = MainErrors.getError(MainErrorType.INVALID_ACCESS_TOKEN, (Locale)request.getAttribute("locale"));
			messageConverter.convertData(request, response, mainError);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			doHandle(request, response, authException);
		}
	}

	public ErrorRequestMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(ErrorRequestMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
}
