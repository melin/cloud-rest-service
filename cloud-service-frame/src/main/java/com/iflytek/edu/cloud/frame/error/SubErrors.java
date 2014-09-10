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
 * 日    期：12-2-11
 */
package com.iflytek.edu.cloud.frame.error;

import java.util.EnumMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SubErrors {

    protected static Logger logger = LoggerFactory.getLogger(SubErrors.class);

    //子错误和主错误对应Map,key为子错误代码，值为主错误代码
    private static final EnumMap<SubErrorType, MainErrorType> SUBERROR_MAINERROR_MAPPINGS =
            new EnumMap<SubErrorType, MainErrorType>(SubErrorType.class);

    static {
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_SERVICE_TIMEOUT, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);

        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_PARAMETERS_MISMATCH, MainErrorType.INVALID_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_INVALID_PARAMETER, MainErrorType.INVALID_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_MISSING_PARAMETER, MainErrorType.MISSING_REQUIRED_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_CYCORE_ERROR, MainErrorType.CYCORE_ERROR);
    }

    private static MessageSourceAccessor messageSourceAccessor;

    public static void setErrorMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        SubErrors.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * 获取对应子错误的主错误
     *
     * @param subErrorCode
     * @param locale
     * @return
     */
    public static MainError getMainError(SubErrorType subErrorType, Locale locale) {
        return MainErrors.getError(SUBERROR_MAINERROR_MAPPINGS.get(subErrorType), locale);
    }


    /**
     * @param subErrorCode 子错误代码
     * @param subErrorKey  子错误信息键
     * @param locale       本地化
     * @param params       本地化消息参数
     * @return
     */
    public static SubError getSubError(String subErrorKey, Locale locale, Object... params) {
        try {
            String parsedSubErrorMessage = messageSourceAccessor.getMessage(subErrorKey, params, locale);
            return new SubError(subErrorKey, parsedSubErrorMessage);
        } catch (NoSuchMessageException e) {
            logger.error("不存在对应的错误键：{}，请检查是否正确配置了应用的错误资源，" +
                    "默认位置：i18n/rop/ropError", subErrorKey);
            throw e;
        }
    }
}

