/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
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
