/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.event.EventHub;
import com.monalisa.cqrsframework.exception.ComponentException;
import com.monalisa.cqrsframework.util.Constant;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.event.EventInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * EventRegister
 */
@Component
public class EventRegister implements IRegister {

    @Autowired
    private EventHub eventHub;

    @Override
    public void doRegistration(Class<?> targetClz) {
        Class<?> eventClz = getEventFromExecutor(targetClz);
        EventInvocation eventInvocation = new EventInvocation();
        //SpringApplicationContextHelper.getBean(EventInvocation.class);
        //处理被代理的bean，如Spring的动态代理，否则Spring动态代理后的targetClz将会失去代理增加的功能，如事务支持。
        /*eventInvocation.setEventHandler((IEventHandler) ProxyTargetUtils
                .enhanceTargetForInterface(SpringApplicationContextHelper.getBean(targetClz), IEventHandler.class));*/
        //eventInvocation.setEventHandler((IEventHandler)SpringApplicationContextHelper.getBean(targetClz));

        Method[] methods = targetClz.getMethods();
        Optional<Method> optionalMethod = Arrays.stream(methods).filter(method ->
                method.getName().equals(Constant.EVENT_EXE_METHOD) &&
                        method.getParameterCount() == 1 /*&&
                        Event.class.isAssignableFrom(method.getParameterTypes()[0])*/).findFirst();
        if (optionalMethod.isPresent()) {
            eventInvocation.setMethod(optionalMethod.get());
            eventInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));
        }else{
            throw new RuntimeException("Class <"+targetClz.getName()+">事件处理器的签名错误，方法名一定是execute，参数有且只有一个Event");
        }

        eventHub.register(eventClz, eventInvocation);
    }

    private Class<?> getEventFromExecutor(Class<?> eventExecutorClz) {
        Method[] methods = eventExecutorClz.getDeclaredMethods();
        for (Method method : methods) {
            if (isExecuteMethod(method)) {
                return checkAndGetEventParamType(method);
            }
        }
        throw new ComponentException("Event param in " + eventExecutorClz + " " + Constant.COMMAND_EXE_METHOD
                + "() is not detected");
    }

    private boolean isExecuteMethod(Method method) {
        return Constant.COMMAND_EXE_METHOD.equals(method.getName()) && !method.isBridge();
    }

    protected Class checkAndGetEventParamType(Method method) {
        Class<?>[] exeParams = method.getParameterTypes();
        if (exeParams.length == 0) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should at least have one parameter");
        }
        /*if (!Event.class.isAssignableFrom(exeParams[0])) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should be the subClass of Event");
        }*/
        return exeParams[0];
    }
}
