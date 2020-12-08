/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.dto.Query;
import com.monalisa.cqrsframework.query.IQueryInterceptor;
import com.monalisa.cqrsframework.query.PreQueryInterceptor;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.query.QueryHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PreInterceptorRegister
 */
@Component
public class PreQueryInterceptorRegister implements IRegister {

    @Autowired
    private QueryHub queryHub;


    @Override
    public void doRegistration(Class<?> targetClz) {
        IQueryInterceptor queryInterceptor = (IQueryInterceptor) SpringApplicationContextHelper.getBean(targetClz);
                PreQueryInterceptor preQueryInterceptorAnn = targetClz.getDeclaredAnnotation(PreQueryInterceptor.class);
        Class<? extends Query>[] supportClasses = preQueryInterceptorAnn.querys();
        registerInterceptor(supportClasses, queryInterceptor);
    }

    private void registerInterceptor(Class<? extends Query>[] supportClasses, IQueryInterceptor queryInterceptor) {
        if (null == supportClasses || supportClasses.length == 0) {
            queryHub.registerGlobalPreInterceptors(queryInterceptor);
            return;
        }
        for (Class<? extends Query> supportClass : supportClasses) {
            queryHub.registerPreInterceptors(supportClass, queryInterceptor);
        }
    }
}
