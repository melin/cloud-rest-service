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
