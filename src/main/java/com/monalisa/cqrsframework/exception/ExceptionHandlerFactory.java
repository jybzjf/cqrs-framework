package com.monalisa.cqrsframework.exception;

import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * ExceptionHandlerFactory
 */
public class ExceptionHandlerFactory {

    public static IExceptionHandler getExceptionHandler() {
        try {
            return SpringApplicationContextHelper.getBean(IExceptionHandler.class);
        } catch (NoSuchBeanDefinitionException ex) {
            return DefaultExceptionHandler.singleton;
        }
    }
}
