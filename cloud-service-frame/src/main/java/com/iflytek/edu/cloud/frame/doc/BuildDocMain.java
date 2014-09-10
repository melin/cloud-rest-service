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
package com.iflytek.edu.cloud.frame.doc;

import static java.io.File.separator;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iflytek.edu.cloud.frame.utils.EnvUtil;
import com.iflytek.edu.cloud.frame.utils.SystemPropertyUtil;

/**
 * Create on @2014年7月16日 @下午10:03:11 
 * @author libinsong1204@gmail.com
 */
public class BuildDocMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDocBuilder.class);
	
	public static void main(String[] args) throws Exception {
		ServiceDocBuilder docBuilder = new ServiceDocBuilder();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		File jsonFile = getJsonFile();
		JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(jsonFile, JsonEncoding.UTF8);
		jsonGenerator.writeObject(docBuilder.buildDoc());
		
		LOGGER.info("服务API文档信息：" + jsonFile.getPath());
	}
	
	private static File getJsonFile() {
		String docPath = null;
		if(EnvUtil.isDevelopment()) {
			docPath = EnvUtil.getProjectBaseDir() + separator + 
					"src" + separator + "main" + separator + "resources" + separator
					+ EnvUtil.getProjectName() + ".json";
		} else {
			docPath = SystemPropertyUtil.get("BASE_HOME") + separator + "conf" + separator
					+ EnvUtil.getProjectName() + ".json";
		}
		
		return new File(docPath);
	}
}
