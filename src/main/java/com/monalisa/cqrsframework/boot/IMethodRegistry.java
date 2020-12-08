package com.monalisa.cqrsframework.boot;

import java.lang.reflect.Method;

/**
 * @author lverpeng
 * @since 2019-03-01
 */
public interface IMethodRegistry {
    /**
     * 注册方法
     *
     * @param targetClz
     * @param method
     */
    void doRegistration(Class<?> targetClz, Method method);
}
