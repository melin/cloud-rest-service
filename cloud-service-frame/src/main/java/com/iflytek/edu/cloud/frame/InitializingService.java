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
