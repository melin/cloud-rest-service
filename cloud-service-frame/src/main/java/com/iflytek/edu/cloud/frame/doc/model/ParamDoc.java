/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.doc.model;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class ParamDoc {
	private String name;
	private String dataType;
	private String exampleData;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getExampleData() {
		return exampleData;
	}
	public void setExampleData(String exampleData) {
		this.exampleData = exampleData;
	}
}
