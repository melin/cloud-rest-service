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
