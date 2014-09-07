/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.spring;

import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class Jaxb2RootElementHttpMessageConverterExt extends
		Jaxb2RootElementHttpMessageConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Jaxb2RootElementHttpMessageConverterExt.class);
	
	@Override
	protected void customizeMarshaller(Marshaller marshaller) {
		try {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		} catch (PropertyException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
