/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.iflytek.edu.cloud.frame.spring.DelegatingFilterProxyExt;
import com.iflytek.edu.cloud.frame.spring.ProfileApplicationContextInitializer;
import com.iflytek.edu.cloud.frame.utils.EnvUtil;
import com.iflytek.edu.cloud.frame.web.listener.LogBackLoadConfigureListener;
import com.iflytek.edu.cloud.frame.web.servlet.PrintProjectVersionServlet;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class RestServiceWebApplicationInitializer implements
		WebApplicationInitializer {
	private static final Logger logger = LoggerFactory.getLogger(RestServiceWebApplicationInitializer.class);

    @Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		servletContext.setInitParameter("contextConfigLocation", "classpath*:META-INF/spring/*-context.xml");  
		servletContext.setInitParameter("contextInitializerClasses", ProfileApplicationContextInitializer.class.getName());  
		servletContext.addListener(new LogBackLoadConfigureListener());
		servletContext.addListener(new ContextLoaderListener());
		
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter());
        EnumSet<DispatcherType> characterEncodingFilterDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");
        characterEncodingFilter.addMappingForUrlPatterns(characterEncodingFilterDispatcherTypes, true, "/*");
		
		FilterRegistration.Dynamic openServiceFilter = servletContext.addFilter("openServiceFilter", new DelegatingFilterProxy());
        EnumSet<DispatcherType> openServiceFilterDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        openServiceFilter.addMappingForUrlPatterns(openServiceFilterDispatcherTypes, true, "/api");
        
        if(EnvUtil.jdbcEnabled()) {
	        FilterRegistration.Dynamic serviceMetricsFilter = servletContext.addFilter("serviceMetricsFilter", new DelegatingFilterProxy());
	        EnumSet<DispatcherType> serviceMetricsFilterDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
	        serviceMetricsFilter.addMappingForUrlPatterns(serviceMetricsFilterDispatcherTypes, true, "/api");
        }
        
        FilterRegistration.Dynamic CORSFilter = servletContext.addFilter("CORSFilter", new DelegatingFilterProxy());
        EnumSet<DispatcherType> CORSFilterDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        CORSFilter.addMappingForUrlPatterns(CORSFilterDispatcherTypes, true, "/api");
        
        if(EnvUtil.oauthEnabled()) {
        	FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxyExt());
        	EnumSet<DispatcherType> springSecurityFilterChainDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        	springSecurityFilterChain.addMappingForUrlPatterns(springSecurityFilterChainDispatcherTypes, true, "/api");
        } else {
        	logger.info("没有启动oauth2认证，需要启动，请在META-INF/res/profile.properties文件中添加oauth2 profile");
        }
        
        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet("rest", new DispatcherServlet());
        dispatcherServlet.setLoadOnStartup(1);
        dispatcherServlet.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        dispatcherServlet.setInitParameter("contextConfigLocation", "org.spring.rest");
        dispatcherServlet.setMultipartConfig(getMultiPartConfig());
        dispatcherServlet.addMapping("/api");
        
        ServletRegistration.Dynamic printProjectVersionServlet = servletContext.addServlet("printProjectVersionServlet", new PrintProjectVersionServlet());
        printProjectVersionServlet.setLoadOnStartup(Integer.MAX_VALUE);
	}
    
    private MultipartConfigElement getMultiPartConfig() {
        String location = System.getProperty("java.io.tmpdir");
        	
        long maxFileSize = -1;
        long maxRequestSize = -1;
        int fileSizeThreshold = -1;
        return new MultipartConfigElement(
            location,
            maxFileSize,
            maxRequestSize,
            fileSizeThreshold
        );
    }
}
