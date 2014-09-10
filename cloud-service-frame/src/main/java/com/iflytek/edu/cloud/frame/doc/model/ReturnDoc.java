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
package com.iflytek.edu.cloud.frame.doc.model;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class ReturnDoc {
	private String dataType;
	private String exampleData;
	private String description;
	private int dimensions;
	private String beanName;
	
	public boolean isArray() {
        return dimensions > 0;
    }
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDimensions() {
		return dimensions;
	}
	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getExampleData() {
		return exampleData;
	}
	public void setExampleData(String exampleData) {
		this.exampleData = exampleData;
	}
}
