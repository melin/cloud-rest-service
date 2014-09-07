/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.web.filter;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Multimap;
import com.iflytek.edu.cloud.frame.InitializingService;
import com.iflytek.edu.cloud.frame.error.MainErrorType;
import com.iflytek.edu.cloud.frame.error.support.ErrorRequestMessageConverter;
import com.iflytek.edu.cloud.frame.spring.Jaxb2RootElementHttpMessageConverterExt;
import com.iflytek.edu.cloud.frame.utils.ErrorMsgParser;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class CheckOpenServiceFilterTest {
	
	private static CheckOpenServiceFilter filter;
	
	@BeforeClass
	public static void init() {
		InitializingService initializingService = new InitializingService();
		try {
			initializingService.afterPropertiesSet();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ErrorRequestMessageConverter messageConverter = new ErrorRequestMessageConverter();
		messageConverter.setJsonMessageConverter(new MappingJackson2HttpMessageConverter());
		messageConverter.setXmlMessageConverter(new Jaxb2RootElementHttpMessageConverterExt());
		
		filter = new CheckOpenServiceFilter();
		filter.setMessageConverter(messageConverter);
		
		try {
			Field field = filter.getClass().getDeclaredField("methodVersionMap");
			field.setAccessible(true);
			Multimap<String, String> methodVersionMap = (Multimap<String, String>) field.get(filter);
			
			methodVersionMap.put("user.get", "1.0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试服务只支持Get和Post，不支持PUT或者DELETE
	 */
	@Test
    @Ignore
	public void testHttpActionGetAndPost() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("PUT");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.HTTP_ACTION_NOT_ALLOWED.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试服务缺少method参数
	 */
	@Test
    @Ignore
	public void testMethodIsNull() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("POST");
			request.addParameter("version", "1.0.0");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.MISSING_METHOD.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试服务不存在
	 */
	@Test
    @Ignore
	public void testMethodNotExist() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("POST");
			request.addParameter("method", "user.add");
			request.addParameter("version", "1.0.0");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.INVALID_METHOD.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试服务缺少version参数
	 */
	@Test
    @Ignore
	public void testVersionIsNull() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("POST");
			request.addParameter("method", "user.get");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.MISSING_VERSION.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试找不到对应version服务
	 */
	@Test
    @Ignore
	public void testVersionNotExist() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("POST");
			request.addParameter("method", "user.get");
			request.addParameter("version", "1.0.1");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.UNSUPPORTED_VERSION.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试服务缺少access_token参数
	 */
	@Test
    @Ignore
	public void testAccessTokenIsNull() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		
		try {
			request.setMethod("POST");
			request.addParameter("method", "user.get");
			request.addParameter("version", "1.0.0");
			filter.doFilter(request, response, null);
			
			Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
			Assert.assertEquals(MainErrorType.MISSING_ACCESS_TOKEN.value(), ErrorMsgParser.getErrorCode(response));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
