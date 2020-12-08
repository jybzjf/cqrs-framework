/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.dto.Query;
import com.monalisa.cqrsframework.exception.ComponentException;
import com.monalisa.cqrsframework.query.QueryHub;
import com.monalisa.cqrsframework.query.QueryInvocation;
import com.monalisa.cqrsframework.util.Constant;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;


/**
 * QueryRegister
 */

@Component
@DependsOn({"preQueryInterceptorRegister", "postQueryInterceptorRegister"})
public class QueryRegister implements IRegister {

    @Autowired
    private QueryHub queryHub;

    @Override
    public void doRegistration(Class<?> targetClz) {
        Class<? extends Query> queryClz = getQueryFromExecutor(targetClz);
        QueryInvocation queryInvocation = new QueryInvocation();
        Method[] methods = targetClz.getMethods();
        Optional<Method> optionalMethod = Arrays.stream(methods).filter(method ->
                method.getName().equals(Constant.QUERY_EXE_METHOD) &&
                        method.getParameterCount() == 1 &&
                        Query.class.isAssignableFrom(method.getParameterTypes()[0])).findFirst();
        if (optionalMethod.isPresent()) {
            queryInvocation.setMethod(optionalMethod.get());
            queryInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));
        }else{
            throw new RuntimeException("Class <"+targetClz.getName()+">命令处理器的签名错误，方法名一定是execute，参数有且只有一个Query");
        }
        queryHub.register(queryClz, queryInvocation);
    }

    private Class<? extends Query> getQueryFromExecutor(Class<?> queryExecutorClz) {
        Method[] methods = queryExecutorClz.getDeclaredMethods();
        for (Method method : methods) {
            if (isExecuteMethod(method)) {
                Class queryClz = checkAndGetQueryParamType(method);
                //queryHub.registerResponseRepository(queryClz, method.getReturnType());
                return (Class<? extends Query>) queryClz;
            }
        }
        throw new ComponentException(" There is no " + Constant.QUERY_EXE_METHOD + "() in " + queryExecutorClz);
    }

    private boolean isExecuteMethod(Method method) {
        return Constant.QUERY_EXE_METHOD.equals(method.getName()) && !method.isBridge();
    }

    protected Class checkAndGetQueryParamType(Method method) {
        Class<?>[] exeParams = method.getParameterTypes();
        if (exeParams.length == 0) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should at least have one parameter");
        }
        if (!Query.class.isAssignableFrom(exeParams[0])) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should be the subClass of Query");
        }
        return exeParams[0];
    }

}
