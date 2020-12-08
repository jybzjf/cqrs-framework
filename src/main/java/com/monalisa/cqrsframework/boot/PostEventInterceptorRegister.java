/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.event.EventHub;
import com.monalisa.cqrsframework.event.PostEventInterceptor;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.event.IEventInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PostInterceptorRegister
 */
@Component
public class PostEventInterceptorRegister implements IRegister{

    @Autowired
    private EventHub eventhub;

    //private ApplicationContext applicationContext;

    @Override
    public void doRegistration(Class<?> targetClz) {
        //处理被代理的bean，如Spring的动态代理，否则Spring动态代理后的targetClz将会失去代理增加的功能，如事务支持。
        /*IEventInterceptor eventInterceptor = (IEventInterceptor) ProxyTargetUtils
                .enhanceTargetForInterface(applicationContext.getBean(targetClz),IEventInterceptor.class);*/
        IEventInterceptor eventInterceptor = (IEventInterceptor) SpringApplicationContextHelper.getBean(targetClz);
        PostEventInterceptor postInterceptorAnn = targetClz.getDeclaredAnnotation(PostEventInterceptor.class);
        Class<?>[] supportClasses = postInterceptorAnn.events();
        registerInterceptor(supportClasses, eventInterceptor);
    }

    private void registerInterceptor(Class<?>[] supportClasses, IEventInterceptor commandInterceptor) {
        if (null == supportClasses || supportClasses.length == 0) {
            eventhub.registerGlobalPostInterceptors(commandInterceptor);
            return;
        }
        for (Class<?> supportClass : supportClasses) {
            eventhub.registerPostInterceptors(supportClass, commandInterceptor);
        }
    }
}
