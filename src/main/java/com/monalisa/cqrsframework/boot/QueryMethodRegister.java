/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.dto.Query;
import com.monalisa.cqrsframework.query.QueryInvocation;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.query.QueryHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * QueryRegister
 */

@Component("queryMethodRegister")
public class QueryMethodRegister extends QueryRegister implements IMethodRegistry {

    @Autowired
    private QueryHub queryHub;

    @Override
    public void doRegistration(Class<?> targetClz, Method method) {
        Class<? extends Query> queryClz = checkAndGetQueryParamType(method);
        QueryInvocation queryInvocation = new QueryInvocation();
        queryInvocation.setMethod(method);
        queryInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));

        queryHub.register(queryClz, queryInvocation);
    }
}
