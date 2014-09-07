/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.web.filter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.iflytek.edu.cloud.frame.Constants;
import com.iflytek.edu.cloud.frame.error.MainError;
import com.iflytek.edu.cloud.frame.error.MainErrorType;
import com.iflytek.edu.cloud.frame.error.MainErrors;
import com.iflytek.edu.cloud.frame.error.support.ErrorRequestMessageConverter;
import com.iflytek.edu.cloud.frame.spring.rest.ServiceMethodHandlerMapping;
import com.iflytek.edu.cloud.frame.spring.rest.ServiceMethodInfo;
import com.iflytek.edu.cloud.frame.support.ServiceRequestLogging;
import com.iflytek.edu.cloud.frame.utils.EnvUtil;
import com.iflytek.edu.cloud.frame.utils.RestContextHolder;

/**
 * 检测请求服务参数是否正确，测试：format、method、version、access_token、appkey
 * 
 * 通过反射从RequestMappingHandlerMapping中获取RequestMappingInfo信息。
 * 服务请求时，通过RequestMappingInfo信息验证method和version是否正确。
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class CheckOpenServiceFilter implements Filter, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckOpenServiceFilter.class);
	private ErrorRequestMessageConverter messageConverter;
	private ServiceMethodHandlerMapping handlerMapping;
	
	private Multimap<String, String> methodVersionMap = ArrayListMultimap.create(); 
	
	private ServiceRequestLogging requestLogging = new ServiceRequestLogging();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("通过反射从RequestMappingHandlerMapping中获取RequestMappingInfo信息");
		
		Field urlMapField = AbstractHandlerMethodMapping.class.getDeclaredField("urlMap");
		urlMapField.setAccessible(true);
		LinkedMultiValueMap<String, ServiceMethodInfo> urlMap = 
				(LinkedMultiValueMap<String, ServiceMethodInfo>) urlMapField.get(handlerMapping);
		
		for(Entry<String, List<ServiceMethodInfo>> entry : urlMap.entrySet()) {
			List<ServiceMethodInfo> mapping = entry.getValue();
			for(ServiceMethodInfo mappingInfo : mapping) {
				methodVersionMap.put(entry.getKey(), mappingInfo.getServiceMethodCondition().getVersion());
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		MainError mainError = null;
		Locale locale = getLocale(request);
		
		String format = request.getParameter(Constants.SYS_PARAM_KEY_FORMAT);
		if(!StringUtils.hasText(format)) {
			format = Constants.DATA_FORMAT_XML;
		}
		
		request.setAttribute(Constants.SYS_PARAM_KEY_LOCALE, locale);
		request.setAttribute(Constants.SYS_PARAM_KEY_FORMAT, format);
		
		String appkey = request.getParameter(Constants.SYS_PARAM_KEY_APPKEY);
		if(!StringUtils.hasText(appkey)) {
			mainError = MainErrors.getError(MainErrorType.MISSING_APPKEY, locale);
		} else {
			RestContextHolder.getContext().addParam(Constants.SYS_PARAM_KEY_APPKEY, appkey);
		}
		
		//只支持GET, POST
		if(mainError == null) {
			String httpMethod = httpServletRequest.getMethod();
			if(!Constants.HTTP_METHOD_GET.equals(httpMethod) && 
					!Constants.HTTP_METHOD_POST.equals(httpMethod)) {
				mainError = MainErrors.getError(MainErrorType.HTTP_ACTION_NOT_ALLOWED, locale);
			}
		}
		
		//判断service method、version方法是否存在
		if(mainError == null) {
			String method = request.getParameter(Constants.SYS_PARAM_KEY_METHOD);
	        if (!StringUtils.hasText(method)) {
	        	//缺少method参数
	        	mainError = MainErrors.getError(MainErrorType.MISSING_METHOD, locale);
	        } else {
	        	if(methodVersionMap.containsKey(method)) {
	        		String version = request.getParameter(Constants.SYS_PARAM_KEY_VERSION);
	        		
	        		if(!StringUtils.hasText(version)) {
	        			//版本号为空
	        			mainError = MainErrors.getError(MainErrorType.MISSING_VERSION, locale);
	        		} else {
		        		if(!methodVersionMap.get(method).contains(version)) {
		        			//对应版本号不存在
		        			mainError = MainErrors.getError(MainErrorType.UNSUPPORTED_VERSION, locale);
		        		}
	        		}
	        	} else {
	        		//method不存在
	        		mainError = MainErrors.getError(MainErrorType.INVALID_METHOD, locale);
	        	}
	        }
		}
        
        //判断access_token是否为空
        if(mainError == null) {
            if(EnvUtil.oauthEnabled()){
                String accessToken = request.getParameter(Constants.SYS_PARAM_KEY_ACCESS_TOKEN);
                if(!StringUtils.hasText(accessToken)) {
                    mainError = MainErrors.getError(MainErrorType.MISSING_ACCESS_TOKEN, locale);
                }
            }
        }
        
        //jsonp
      	String callback = httpServletRequest.getParameter(Constants.SYS_PARAM_KEY_CALLBACK);
      	if(StringUtils.hasText(callback))
      		RestContextHolder.getContext().addParam(Constants.SYS_PARAM_KEY_CALLBACK, callback);
      	
        if(mainError != null) {
        	request.setAttribute(Constants.MAIN_ERROR_CODE, mainError.getCode());
        	httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	messageConverter.convertData(httpServletRequest, httpServletResponse, mainError);
        } else {
        	chain.doFilter(request, response);
        }
        
        requestLogging.recoredLog(httpServletRequest, httpServletResponse);
        RestContextHolder.clearContext();
	}
	
	private Locale getLocale(ServletRequest request) {
		String localePart = request.getParameter(Constants.SYS_PARAM_KEY_LOCALE);
		if(!StringUtils.hasText(localePart))
			localePart = "zh_CN";
		
		Locale locale = StringUtils.parseLocaleString(localePart);
		return locale;
	}

	@Override
	public void destroy() {
		
	}

	public ServiceMethodHandlerMapping getHandlerMapping() {
		return handlerMapping;
	}

	public void setHandlerMapping(ServiceMethodHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	public ErrorRequestMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(ErrorRequestMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@SuppressWarnings("serial")
	public static class SimpleAbstractAuthenticationToken extends AbstractAuthenticationToken {
		private String appkey;
		
		public SimpleAbstractAuthenticationToken(String appkey) {
			super(null);
			this.appkey = appkey;
		}
		
		public SimpleAbstractAuthenticationToken(
				Collection<? extends GrantedAuthority> authorities) {
			super(authorities);
		}

		@Override
		public Object getCredentials() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getPrincipal() {
			return appkey;
		}
		
	}
}
