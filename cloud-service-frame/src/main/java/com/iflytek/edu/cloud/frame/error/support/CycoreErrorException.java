/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.error.support;

/**
 *
 * Cycore业务异常
 * @author dsliu@iflytek.com
 *
 */
@SuppressWarnings("serial")
public class CycoreErrorException extends RuntimeException{

	private String errorMessage;

	public CycoreErrorException(String errorMessage) {
		this(errorMessage, errorMessage);
    }

	public CycoreErrorException(String errorMessage, String message) {
		super(message);
		this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
