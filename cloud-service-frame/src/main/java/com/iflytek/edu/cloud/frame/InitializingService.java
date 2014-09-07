/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.iflytek.edu.cloud.frame.error.MainErrors;
import com.iflytek.edu.cloud.frame.error.SubErrors;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class InitializingService implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializingService.class);
	
	private static final String I18N_ROP_ERROR = "META-INF/i18n/error";

	@Override
	public void afterPropertiesSet() throws Exception {
		//hibernate validator 显示中文
		Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
		
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR);
        LOGGER.info("加载资源文件：" + I18N_ROP_ERROR);
        
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
	}

}
