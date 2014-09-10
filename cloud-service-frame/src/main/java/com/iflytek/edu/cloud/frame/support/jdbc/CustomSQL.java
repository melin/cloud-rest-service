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
package com.iflytek.edu.cloud.frame.support.jdbc;

import httl.Engine;
import httl.Template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 在开发的时，设置虚拟机参数-DreloadSQLFiles=true. 可以重新加载sql语句配置文件，
 * 避免修改配置文件，需要重新启动服务器。
 *
 * @author   libinsong1204@gmail.com
 * @Date	 2011-6-14 下午01:41:33
 */
public class CustomSQL implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(CustomSQL.class);

	private Map<String, SQLBean> _sqlPool = new ConcurrentHashMap<String, SQLBean>();

	private static final String STRING_SPACE = " ";
	
	private final SAXReader saxReader = new SAXReader();
	private Engine engine = null;
	
	private Map<String, Long> configMap = new HashMap<String, Long>();
	private boolean reloadSQLFiles = false;
	
	private static CustomSQL instance = null;
	
	private CustomSQL() {
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		instance = context.getBean(CustomSQL.class);
		instance.init();
	}
	
	public static CustomSQL getInstance() {
		if(instance == null) {
			synchronized (CustomSQL.class) {
				if(instance == null) {
					instance = new CustomSQL();
					instance.init();
				}
			}
		}
		
		return instance;
	}

	public void init() {
		reloadSQLFiles = Boolean.valueOf(System.getProperty("reloadSQLFiles"));
		
		try {
			engine = Engine.getEngine();

	        Resource[] configs = loadConfigs();
			for (Resource _config : configs) {
				logger.info("Loading " + _config.getURL().getPath());
				configMap.put(_config.getURL().getPath(), _config.lastModified());
				read(_config.getInputStream());
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}
	
	protected Resource[] loadConfigs() throws IOException {
		PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		return patternResolver.getResources("classpath*:custom-sql/**/*.xml");
	}

	public String get(String id) {
		if(reloadSQLFiles) {
			try {
				reloadConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		SQLBean bean = _sqlPool.get(id);
		
		if(bean == null)
			throw new IllegalStateException("sql id 不存在：" + id);
		
		if("simple".equals(bean.getTempateType())) {
			return _sqlPool.get(id).getContent();
		} else
			throw new RuntimeException("SQL 模板类型不正确，只可以是simple类型");
	}
	
	public String get(String id, Map<String, Object> parameters) {
		if(reloadSQLFiles) {
			try {
				reloadConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			SQLBean bean = _sqlPool.get(id);
			
			String result = (String) bean.getTemplate().evaluate(parameters);
			return result;
        } catch (ParseException e) {
        	throw new RuntimeException("Parse sql failed", e);  
		}  
	}
	
	/**
	 * 更新文件修改时间刷新。
	 */
	private void reloadConfig() throws IOException {
		Resource[] newConfigs = this.loadConfigs();
		for(Resource newConfig : newConfigs) {
			boolean flag = true;
			for(Entry<String, Long> entry : configMap.entrySet()) {
				if(newConfig.getURL().getPath().equals(entry.getKey())) {
					flag = false;
				
					if (newConfig.getFile().lastModified() != entry.getValue().longValue()) {
						configMap.put(entry.getKey(), newConfig.getFile().lastModified());
						read(newConfig.getInputStream());
						logger.info("Reloading " + entry.getKey());
						
						break;
					}
				}
			}
			
			if(flag) {
				configMap.put(newConfig.getURL().getPath(), newConfig.getFile().lastModified());
				read(newConfig.getInputStream());
				logger.info("Reloading " + newConfig.getURL().getPath());
			}
		}
	}

	protected void read(InputStream is) {

		if (is == null) return;

		Document document;
		try {
			document = saxReader.read(is);
		} catch (DocumentException e) {
			throw new RuntimeException(e.getMessage(), e);  
		}
		
		Element rootElement = document.getRootElement();

		for (Object sqlObj : rootElement.elements("sql")) {
			Element sqlElement = (Element)sqlObj;
			String id = sqlElement.attributeValue("id");
			String tempateType = sqlElement.attributeValue("tempateType");
			
			if("simple".equals(tempateType) || "httl".equals(tempateType)) {
				String content = transform(sqlElement.getText());
				
				SQLBean bean = new SQLBean();
				bean.setTempateType(tempateType);
				bean.setContent(content);
				
				try {
					Template template = engine.parseTemplate(content);
					bean.setTemplate(template);
					_sqlPool.put(id, bean);
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else {
				logger.warn("{} 对应 tempateType 值 {} 不正确，可选值为：simple和httl", id, tempateType);
			}
		}
	}

	protected String transform(String sql) {
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader bufferedReader =
				new BufferedReader(new StringReader(sql));

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line.trim());
				sb.append(STRING_SPACE);
			}

			bufferedReader.close();
		}
		catch (IOException ioe) {
			return sql;
		}

		return sb.toString().trim();
	}
	
	public Map<String, SQLBean> getAllSQL() {
		return _sqlPool;
	}
	
	public static class SQLBean {
		/**
		 * 两种可选类型：simple和httl
		 */
		private String tempateType = "simple";

		private String content = "";
		
		private Template template;
		
		
		public String getTempateType() {
			return tempateType;
		}
		public void setTempateType(String tempateType) {
			this.tempateType = tempateType;
		}
		public Template getTemplate() {
			return template;
		}
		public void setTemplate(Template template) {
			this.template = template;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
	}
}