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
/**
 *
 * 日    期：12-2-14
 */
package com.iflytek.edu.cloud.frame.error;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@XmlRootElement(name = "mainError")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SimpleMainError implements MainError {

	@XmlElement
    private String code;
	@XmlElement
    private String message;
	@XmlElement
    private String solution;

    private List<SubError> subErrors = new ArrayList<SubError>();
    
    public SimpleMainError() {
	}

	public SimpleMainError(String code, String message, String solution) {
        this.code = code;
        this.message = message;
        this.solution = solution;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSolution() {
        return solution;
    }

    public List<SubError> getSubErrors() {
        return this.subErrors;
    }

    public void setSubErrors(List<SubError> subErrors) {
        this.subErrors = subErrors;
    }

    public MainError addSubError(SubError subError) {
        this.subErrors.add(subError);
        return this;
    }

	@Override
	public String toString() {
		return "MainError [code=" + code + ", message=" + message + "]";
	}
}

