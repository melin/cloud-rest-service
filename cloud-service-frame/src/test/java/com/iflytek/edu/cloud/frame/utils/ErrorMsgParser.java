/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.utils;

import java.io.ByteArrayInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.mock.web.MockHttpServletResponse;

public class ErrorMsgParser {
	
	public static String getErrorCode(MockHttpServletResponse response) {
		try {
			SAXReader saxReader = new SAXReader();  
			Document document  = saxReader.read(new ByteArrayInputStream(response.getContentAsByteArray()));
			Element rootElement = document.getRootElement();  
	        return rootElement.element("code").getText();
		} catch (DocumentException e) {
			return null;
		}
	}
}
