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
public enum MainErrorType {
    HTTP_ACTION_NOT_ALLOWED,
    FORBIDDEN_REQUEST,
    METHOD_OBSOLETED,
    BUSINESS_LOGIC_ERROR,
    MISSING_ACCESS_TOKEN,
    INVALID_ACCESS_TOKEN,
    MISSING_METHOD,
    INVALID_METHOD,
    MISSING_VERSION,
    INVALID_VERSION,
    UNSUPPORTED_VERSION,
    INVALID_FORMAT,
    EXCEED_USER_INVOKE_LIMITED,
    EXCEED_SESSION_INVOKE_LIMITED,
    EXCEED_APP_INVOKE_LIMITED,
    EXCEED_APP_INVOKE_FREQUENCY_LIMITED,
    MISSING_APPKEY,
    INVALID_APPKEY,
    
    
    SERVICE_CURRENTLY_UNAVAILABLE,
    
    MISSING_REQUIRED_ARGUMENTS,
    INVALID_ARGUMENTS,
    CYCORE_ERROR;

    private static EnumMap<MainErrorType, String> errorCodeMap = new EnumMap<MainErrorType, String>(MainErrorType.class);

    static {
        errorCodeMap.put(MainErrorType.HTTP_ACTION_NOT_ALLOWED, "101");
        errorCodeMap.put(MainErrorType.FORBIDDEN_REQUEST, "102");
        errorCodeMap.put(MainErrorType.METHOD_OBSOLETED, "103");
        errorCodeMap.put(MainErrorType.BUSINESS_LOGIC_ERROR, "104");
        errorCodeMap.put(MainErrorType.MISSING_ACCESS_TOKEN, "105");
        errorCodeMap.put(MainErrorType.INVALID_ACCESS_TOKEN, "106");
        errorCodeMap.put(MainErrorType.MISSING_METHOD, "107");
        errorCodeMap.put(MainErrorType.INVALID_METHOD, "108");
        errorCodeMap.put(MainErrorType.MISSING_VERSION, "109");
        errorCodeMap.put(MainErrorType.INVALID_VERSION, "110");
        errorCodeMap.put(MainErrorType.UNSUPPORTED_VERSION, "111");
        errorCodeMap.put(MainErrorType.INVALID_FORMAT, "112");
        errorCodeMap.put(MainErrorType.EXCEED_USER_INVOKE_LIMITED, "113");
        errorCodeMap.put(MainErrorType.EXCEED_SESSION_INVOKE_LIMITED, "114");
        errorCodeMap.put(MainErrorType.EXCEED_APP_INVOKE_LIMITED, "115");
        errorCodeMap.put(MainErrorType.EXCEED_APP_INVOKE_FREQUENCY_LIMITED, "116");
        errorCodeMap.put(MainErrorType.MISSING_APPKEY, "117");
        errorCodeMap.put(MainErrorType.INVALID_APPKEY, "118");
        
        errorCodeMap.put(MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE, "201");
        
        errorCodeMap.put(MainErrorType.MISSING_REQUIRED_ARGUMENTS, "301");
        errorCodeMap.put(MainErrorType.INVALID_ARGUMENTS, "302");
        errorCodeMap.put(MainErrorType.CYCORE_ERROR, "310");
    }

    public String value() {
        return errorCodeMap.get(this);
    }
}

