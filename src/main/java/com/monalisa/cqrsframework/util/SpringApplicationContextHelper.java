package com.monalisa.cqrsframework.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContextHelper
 */
@Component
public class SpringApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextHelper.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> targetClz) {
        T beanInstance = null;
        //优先按type查
        try {
            beanInstance = (T) applicationContext.getBean(targetClz);
        } catch (Exception e) {
        }
        //按name查
        if (beanInstance == null) {
            String simpleName = targetClz.getSimpleName();
            //首字母小写
            simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            beanInstance = (T) applicationContext.getBean(simpleName);
        }
        if (beanInstance == null) {
            new RuntimeException("Component " + targetClz + " can not be found in Spring Container");
        }
        return beanInstance;
    }
}
