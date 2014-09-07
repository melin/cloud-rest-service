/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.doc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class MethodDoc {
	private String name;
	private String version;
	private String description;
	private List<ParamDoc> paramDocs = new ArrayList<ParamDoc>();
	private ReturnDoc returnDoc;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ParamDoc> getParamDocs() {
		return paramDocs;
	}
	public void setParamDocs(List<ParamDoc> paramDocs) {
		this.paramDocs = paramDocs;
	}
	public ReturnDoc getReturnDoc() {
		return returnDoc;
	}
	public void setReturnDoc(ReturnDoc returnDoc) {
		this.returnDoc = returnDoc;
	}
}
