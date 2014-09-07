/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.error.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import com.iflytek.edu.cloud.frame.Constants;
import com.iflytek.edu.cloud.frame.error.MainError;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class ErrorRequestMessageConverter {
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;
	
	/**
	 * 转换异常信息为format格式
	 * 
	 * @param httpServletResponse
	 * @param format
	 * @param mainError
	 * @throws IOException
	 */
	public void convertData(HttpServletRequest request, HttpServletResponse httpServletResponse,
			MainError mainError) throws IOException {
		final String format = (String) request.getAttribute(Constants.SYS_PARAM_KEY_FORMAT);
		
		if(Constants.DATA_FORMAT_JSON.equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse(httpServletResponse));
		} else {
			xmlMessageConverter.write(mainError, MediaType.valueOf("application/xml;charset=UTF-8"), 
				new ServletServerHttpResponse(httpServletResponse));
		}
	}

	public MappingJackson2HttpMessageConverter getJsonMessageConverter() {
		return jsonMessageConverter;
	}

	public void setJsonMessageConverter(
			MappingJackson2HttpMessageConverter jsonMessageConverter) {
		this.jsonMessageConverter = jsonMessageConverter;
	}

	public Jaxb2RootElementHttpMessageConverter getXmlMessageConverter() {
		return xmlMessageConverter;
	}

	public void setXmlMessageConverter(
			Jaxb2RootElementHttpMessageConverter xmlMessageConverter) {
		this.xmlMessageConverter = xmlMessageConverter;
	}
}
