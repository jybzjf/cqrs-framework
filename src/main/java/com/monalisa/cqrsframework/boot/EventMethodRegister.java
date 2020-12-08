/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.event.EventHub;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.event.EventInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * CommandRegister
 */

@Component("eventMethodRegister")
public class EventMethodRegister extends EventRegister implements IMethodRegistry {

    @Autowired
    private EventHub eventHub;

    @Override
    public void doRegistration(Class<?> targetClz, Method method) {
        Class<?> eventClz = checkAndGetEventParamType(method);
        EventInvocation eventInvocation = new EventInvocation();
        //ApplicationContextHelper.getBean(EventInvocation.class);

        eventInvocation.setMethod(method);
        eventInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));

        eventHub.register(eventClz, eventInvocation);
    }
}
