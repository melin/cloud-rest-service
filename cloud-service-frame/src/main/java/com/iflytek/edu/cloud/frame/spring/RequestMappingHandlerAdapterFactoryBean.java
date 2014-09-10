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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

/**
 * 创建RequestMappingHandlerAdapter，裁剪不需要的功能。 
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class RequestMappingHandlerAdapterFactoryBean implements FactoryBean<RequestMappingHandlerAdapter>, InitializingBean, ApplicationContextAware {
	private RequestMappingHandlerAdapter handlerAdapter;
	
	private List<HttpMessageConverter<?>> messageConverters;
	
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		handlerAdapter = new RequestMappingHandlerAdapter();
		handlerAdapter.setArgumentResolvers(getDefaultArgumentResolvers());
		handlerAdapter.setInitBinderArgumentResolvers(getDefaultInitBinderArgumentResolvers());
		handlerAdapter.setReturnValueHandlers(getDefaultReturnValueHandlers());
		
		ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
		FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
		conversionServiceFactoryBean.afterPropertiesSet();
		webBindingInitializer.setConversionService(conversionServiceFactoryBean.getObject());
		
		OptionalValidatorFactoryBean validatorFactoryBean = new OptionalValidatorFactoryBean();
		validatorFactoryBean.afterPropertiesSet();
		webBindingInitializer.setValidator(validatorFactoryBean);
		
		handlerAdapter.setWebBindingInitializer(webBindingInitializer);
		handlerAdapter.setApplicationContext(applicationContext);
		handlerAdapter.afterPropertiesSet();
	}

	@Override
	public RequestMappingHandlerAdapter getObject() throws Exception {
		return handlerAdapter;
	}

	@Override
	public Class<RequestMappingHandlerAdapter> getObjectType() {
		return RequestMappingHandlerAdapter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(null, true));
		resolvers.add(new ServletModelAttributeMethodProcessor(true));

		return resolvers;
	}

	private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		return resolvers;
	}

	private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

		handlers.add(new HttpHeadersReturnValueHandler());
		handlers.add(new CallableMethodReturnValueHandler());
		handlers.add(new DeferredResultMethodReturnValueHandler());
		handlers.add(new AsyncTaskMethodReturnValueHandler(null));

		// Annotation-based return value types
		handlers.add(new ModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessorExt(getMessageConverters()));
		
		return handlers;
	}
	
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
