/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.support;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.iflytek.edu.cloud.frame.Constants;
import com.iflytek.edu.cloud.frame.utils.RestContextHolder;
import com.iflytek.edu.cloud.frame.utils.ServiceUtil;
import com.iflytek.edu.cloud.frame.web.filter.ServiceMetricsFilter;

/**
 * 服务请求日志记录
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class ServiceRequestLogging {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("http.request");
	
	/**
	 * @param request
	 * @param response
	 */
	public void recoredLog(HttpServletRequest request, HttpServletResponse response) {
		String clientIp = ServiceUtil.getRemoteAddr(request);
		String locale = ((Locale) request.getAttribute(Constants.SYS_PARAM_KEY_LOCALE)).getDisplayName();
		String format = (String) request.getAttribute(Constants.SYS_PARAM_KEY_FORMAT);
		String appkey = (String) request.getParameter(Constants.SYS_PARAM_KEY_APPKEY);
		String httpMethod = request.getMethod();
		String serviceMethod = request.getParameter(Constants.SYS_PARAM_KEY_METHOD);
		String serviceVersion = request.getParameter(Constants.SYS_PARAM_KEY_VERSION);
		int responseStatus = response.getStatus();
		Long requestTimeMillis = (Long) request.getAttribute(ServiceMetricsFilter.SERVICE_EXEC_TIME);
		
		String mainErrorCode = (String)request.getAttribute(Constants.MAIN_ERROR_CODE);
		
		RestContext context = RestContextHolder.getContext();
        if (StringUtils.hasText(mainErrorCode)) {
        	LOGGER.warn("service request information : mainErrorCode={}, clientIp={}, httpMethod={}, locale={},"
    				+ " appkey={}, serviceMethod={}, serviceVersion={}, format={}, responseStatus={}, requestTimeMillis={},"
    				+ " callCycoreCount={}, callCycoreTime={}",
    				mainErrorCode, clientIp, httpMethod, locale, appkey, serviceMethod, 
    				serviceVersion, format, responseStatus, requestTimeMillis, 
    				context.getCallCycoreCount(), context.getCallCycoreTime());
        } else {
			LOGGER.info("service request information : clientIp={}, httpMethod={}, locale={},"
					+ " appkey={}, serviceMethod={}, serviceVersion={}, format={}, responseStatus={}, requestTimeMillis={}"
					+ " callCycoreCount={}, callCycoreTime={}",
					clientIp, httpMethod, locale, appkey, serviceMethod, 
					serviceVersion, format, responseStatus, requestTimeMillis,
					context.getCallCycoreCount(), context.getCallCycoreTime());
        }
	}

}
