/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
/**
 *
 * 日    期：12-2-23
 */
package com.iflytek.edu.cloud.frame.error;

import java.util.EnumMap;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public enum SubErrorType {
    ISP_SERVICE_UNAVAILABLE,
    ISP_SERVICE_TIMEOUT,

    ISV_MISSING_PARAMETER,
    ISV_INVALID_PARAMETER,
    ISV_CYCORE_ERROR,
    ISV_PARAMETERS_MISMATCH;

    private static EnumMap<SubErrorType, String> errorKeyMap = new EnumMap<SubErrorType, String>(SubErrorType.class);

    static {
        errorKeyMap.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, "isp.xxx-service-unavailable");
        errorKeyMap.put(SubErrorType.ISP_SERVICE_TIMEOUT, "isp.xxx-service-timeout");

        errorKeyMap.put(SubErrorType.ISV_MISSING_PARAMETER, "isv.missing-parameter");
        errorKeyMap.put(SubErrorType.ISV_INVALID_PARAMETER, "isv.invalid-parameter");
        errorKeyMap.put(SubErrorType.ISV_PARAMETERS_MISMATCH, "isv.parameters-mismatch");
        errorKeyMap.put(SubErrorType.ISV_CYCORE_ERROR, "isv.cycore-error");
    }

    public String value() {
        return errorKeyMap.get(this);
    }
}

