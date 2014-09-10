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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.iflytek.edu.cloud.frame.Constants;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class RequestResponseBodyMethodProcessorExt extends RequestResponseBodyMethodProcessor {
	private final static MediaType MEDIA_TYPE_XML = MediaType.valueOf("application/xml;charset=UTF-8");
	
	private final static MediaType MEDIA_TYPE_JSON = MediaType.valueOf("application/json;charset=UTF-8");
	
	public RequestResponseBodyMethodProcessorExt(
			List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	@Override
	protected <T> void writeWithMessageConverters(T returnValue, MethodParameter returnType,
			ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
			throws IOException, HttpMediaTypeNotAcceptableException {
		Class<?> returnValueClass = returnValue.getClass();
		HttpServletRequest servletRequest = inputMessage.getServletRequest();
		String format = servletRequest.getParameter(Constants.SYS_PARAM_KEY_FORMAT);
		
		MediaType contentType = MEDIA_TYPE_XML;
		
		if(Constants.DATA_FORMAT_JSON.equals(format))
			contentType = MEDIA_TYPE_JSON;;
		
		
		if(ClassUtils.isPrimitiveOrWrapper(returnValueClass)) {
			if(Constants.DATA_FORMAT_JSON.equals(format)) {
				String result = "{\"return\":\"" + returnValue + "\"}";
				write(result, contentType, outputMessage);
			} else {
				String result = "<return>" + returnValue + "</return>";
				write(result, contentType, outputMessage);
			}
			
		} else {
		
			for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
				if (messageConverter.canWrite(returnValueClass, contentType)) {
					((HttpMessageConverter<T>) messageConverter).write(returnValue, contentType, outputMessage);
					if (logger.isDebugEnabled()) {
						logger.debug("Written [" + returnValue + "] as \"" + contentType + "\" using [" +
								messageConverter + "]");
					}
					return;
				}
			}
		}
	}
	
	private Charset charset = Charset.forName("UTF-8");
	private void write(final String content, MediaType contentType, ServletServerHttpResponse outputMessage)
			throws IOException, HttpMessageNotWritableException {
		final HttpHeaders headers = outputMessage.getHeaders();
		headers.setContentType(contentType);
		if (headers.getContentLength() == -1) {
			Long contentLength = getContentLength(content, headers.getContentType());
			if (contentLength != null) {
				headers.setContentLength(contentLength);
			}
		}
		
		StreamUtils.copy(content, charset, outputMessage.getBody());
		outputMessage.getBody().flush();
	}
	
	private Long getContentLength(String s, MediaType contentType) {
		try {
			return (long) s.getBytes(charset.name()).length;
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
