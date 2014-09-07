/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.spring;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iflytek.edu.cloud.frame.error.support.CycoreErrorException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.iflytek.edu.cloud.frame.Constants;
import com.iflytek.edu.cloud.frame.error.MainError;
import com.iflytek.edu.cloud.frame.error.MainErrorType;
import com.iflytek.edu.cloud.frame.error.MainErrors;
import com.iflytek.edu.cloud.frame.error.SubError;
import com.iflytek.edu.cloud.frame.error.SubErrorType;
import com.iflytek.edu.cloud.frame.error.SubErrors;
import com.iflytek.edu.cloud.frame.error.support.ErrorRequestMessageConverter;
import com.iflytek.edu.cloud.frame.error.support.InvalidParamException;
import com.iflytek.edu.cloud.frame.error.support.MissingParamException;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
	
	private static final Map<String, SubErrorType> INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS = new LinkedHashMap<String, SubErrorType>();

	private static final ModelAndView ERROR_MODEL_AND_VIEW = new ModelAndView();
	
    static {
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("typeMismatch", SubErrorType.ISV_PARAMETERS_MISMATCH);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotNull", SubErrorType.ISV_MISSING_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotEmpty", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Size", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Range", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Pattern", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Min", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Max", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMin", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMax", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Digits", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Past", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Future", SubErrorType.ISV_INVALID_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("AssertFalse", SubErrorType.ISV_INVALID_PARAMETER);
    }
    
    private ErrorRequestMessageConverter messageConverter;
	
	public DefaultHandlerExceptionResolver() {
		setOrder(Ordered.LOWEST_PRECEDENCE);
	}
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		MainError mainError = null;
		
		Locale locale = (Locale)request.getAttribute(Constants.SYS_PARAM_KEY_LOCALE);
		try {
			if (ex instanceof BindException) {
				mainError = handleBindException((BindException) ex, request, response, handler);
			} else if (ex instanceof MissingParamException) {
				MissingParamException _exception = (MissingParamException)ex;
				mainError = SubErrors.getMainError(SubErrorType.ISV_MISSING_PARAMETER, locale);
				
				SubError subError = SubErrors.getSubError(SubErrorType.ISV_MISSING_PARAMETER.value(), locale,
						_exception.getParamName(), _exception.getMessage());
                mainError.addSubError(subError);
				
			} else if (ex instanceof InvalidParamException) {
				InvalidParamException _exception = (InvalidParamException)ex;
				mainError = SubErrors.getMainError(SubErrorType.ISV_INVALID_PARAMETER, locale);
				
				SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PARAMETER.value(), locale,
						_exception.getParamName(), _exception.getValue(), _exception.getMessage());
                mainError.addSubError(subError);
				
			} else if (ex instanceof CycoreErrorException) {
                CycoreErrorException _exception = (CycoreErrorException)ex;
                mainError = SubErrors.getMainError(SubErrorType.ISV_CYCORE_ERROR, locale);

                SubError subError = SubErrors.getSubError(SubErrorType.ISV_CYCORE_ERROR.value(), locale,
                        _exception.getErrorMessage(),  _exception.getMessage());
                mainError.addSubError(subError);

            } else if (ex instanceof TypeMismatchException) {
				TypeMismatchException _exception = (TypeMismatchException)ex;
				
				mainError = SubErrors.getMainError(SubErrorType.ISV_PARAMETERS_MISMATCH, locale);
				SubErrorType tempSubErrorType = SubErrorType.ISV_PARAMETERS_MISMATCH;
                SubError subError = SubErrors.getSubError(tempSubErrorType.value(), locale,
                		_exception.getValue(), _exception.getRequiredType().getSimpleName());
                mainError.addSubError(subError);
			} else {
				SubError subError = SubErrors.getSubError(SubErrorType.ISP_SERVICE_UNAVAILABLE.value(), 
                		 (Locale)request.getAttribute(Constants.SYS_PARAM_KEY_LOCALE),
                		 request.getParameter(Constants.SYS_PARAM_KEY_METHOD), NestedExceptionUtils.buildMessage(ex.getMessage(), ex));
                 
              	mainError = MainErrors.getError(MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE, 
                		 (Locale)request.getAttribute(Constants.SYS_PARAM_KEY_LOCALE));
                mainError.addSubError(subError);
			}
			
			request.setAttribute(Constants.MAIN_ERROR_CODE, mainError.getCode());
			messageConverter.convertData(request, response, mainError);
			
			logger.warn(ex.getMessage(), ex);
		} catch (Exception handlerException) {
			logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
		}
		
		return ERROR_MODEL_AND_VIEW;
	}
	
	protected MainError handleBindException(BindException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
 		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
 		
 		List<ObjectError> errorList = ex.getBindingResult().getAllErrors();

        //将Bean数据绑定时产生的错误转换为Rop的错误
 		MainError mainError = null;
        if (errorList != null && errorList.size() > 0) {
        	mainError = toMainErrorOfSpringValidateErrors(errorList, (Locale)request.getAttribute(Constants.SYS_PARAM_KEY_LOCALE));
        }
        
        return mainError;
	}
	
	/**
     * 将通过JSR 303框架校验的错误转换为服务框架的错误体系
     *
     * @param allErrors
     * @param locale
     * @return
     */
    private MainError toMainErrorOfSpringValidateErrors(List<ObjectError> allErrors, Locale locale) {
        if (hastSubErrorType(allErrors, SubErrorType.ISV_MISSING_PARAMETER)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_MISSING_PARAMETER);
        } else if (hastSubErrorType(allErrors, SubErrorType.ISV_PARAMETERS_MISMATCH)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_PARAMETERS_MISMATCH);
        } else {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_INVALID_PARAMETER);
        }
    }
    
    /**
     * 判断错误列表中是否包括指定的子错误
     *
     * @param allErrors
     * @param subErrorType1
     * @return
     */
    private boolean hastSubErrorType(List<ObjectError> allErrors, SubErrorType subErrorType1) {
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.containsKey(fieldError.getCode())) {
                    SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                    if (tempSubErrorType == subErrorType1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 生成对应子错误的错误类
     *
     * @param allErrors
     * @param locale
     * @param subErrorType
     * @return
     */
    private MainError getBusinessParameterMainError(List<ObjectError> allErrors, Locale locale, SubErrorType subErrorType) {
        MainError mainError = SubErrors.getMainError(subErrorType, locale);
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                if (tempSubErrorType == subErrorType) {
                    SubError subError = SubErrors.getSubError(tempSubErrorType.value(), locale,
                            fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
                    mainError.addSubError(subError);
                }
            }
        }
        return mainError;
    }

	public ErrorRequestMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(ErrorRequestMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
    
}
