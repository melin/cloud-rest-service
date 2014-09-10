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
