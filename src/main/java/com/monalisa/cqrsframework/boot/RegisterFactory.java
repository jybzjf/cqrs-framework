/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.event.PostEventInterceptor;
import com.monalisa.cqrsframework.command.CommandHandler;
import com.monalisa.cqrsframework.command.PostCommandInterceptor;
import com.monalisa.cqrsframework.command.PreCommandInterceptor;
import com.monalisa.cqrsframework.event.EventHandler;
import com.monalisa.cqrsframework.event.PreEventInterceptor;
import com.monalisa.cqrsframework.query.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RegisterFactory
 *
 * @author fulan.zjf 2017-11-04
 */
@Component
public class RegisterFactory {

    @Autowired
    private PreCommandInterceptorRegister preCommandInterceptorRegister;
    @Autowired
    private PostCommandInterceptorRegister postCommandInterceptorRegister;
    @Autowired
    private PreEventInterceptorRegister preEventInterceptorRegister;
    @Autowired
    private PostEventInterceptorRegister postEventInterceptorRegister;
    @Autowired
    private CommandRegister commandRegister;
    @Autowired
    @Qualifier("commandMethodRegister")
    private IMethodRegistry commandMethodRegistry;
    @Autowired
    @Qualifier("eventMethodRegister")
    private IMethodRegistry eventMethodRegistry;
    @Autowired
    @Qualifier("queryMethodRegister")
    private IMethodRegistry queryMethodRegistry;
    @Autowired
    private EventRegister eventRegister;

    public <T extends IRegister> List getRegister(Class<?> targetClz) {
        List registers = new ArrayList<>();
        /*Annotation[] annotations = targetClz.getAnnotations();
        for (Annotation annotation : annotations) {

        }*/
        //PreCommandInterceptor preCommandInterceptorAnn = targetClz.getDeclaredAnnotation(PreCommandInterceptor.class);
        if(targetClz.isAnnotationPresent(PreCommandInterceptor.class)) {
            registers.add(preCommandInterceptorRegister);
        }
        //PostCommandInterceptor postCommandInterceptorAnn = targetClz.getDeclaredAnnotation(PostCommandInterceptor.class);
        if (targetClz.isAnnotationPresent(PostCommandInterceptor.class)) {
            registers.add(postCommandInterceptorRegister);
        }
        //PreEventInterceptor preEventInterceptor = targetClz.getDeclaredAnnotation(PreEventInterceptor.class);
        if (targetClz.isAnnotationPresent(PreEventInterceptor.class)) {
            registers.add(preEventInterceptorRegister);
        }
        //PostEventInterceptor postEventInterceptor = targetClz.getDeclaredAnnotation(PostEventInterceptor.class);
        if (targetClz.isAnnotationPresent(PostEventInterceptor.class)) {
            registers.add(postEventInterceptorRegister);
        }
        //CommandExecutor commandExecutorAnn = targetClz.getDeclaredAnnotation(CommandExecutor.class);
        if (targetClz.isAnnotationPresent(CommandHandler.class))  {
            registers.add(commandRegister);
        }
        //EventHandler eventHandlerAnn = targetClz.getDeclaredAnnotation(EventHandler.class);
        if (targetClz.isAnnotationPresent(EventHandler.class))  {
            registers.add(eventRegister);
        }
        return registers;
    }

    public IMethodRegistry getCommandMethodRegister(Method method) {
        CommandHandler commandAnn = method.getDeclaredAnnotation(CommandHandler.class);
        if (commandAnn != null) {
            return commandMethodRegistry;
        }
        return null;
    }

    public IMethodRegistry getEventMethodRegister(Method method) {
        EventHandler eventAnn = method.getDeclaredAnnotation(EventHandler.class);
        if (eventAnn != null) {
            return eventMethodRegistry;
        }
        return null;
    }

    public IMethodRegistry getQueryMethodRegister(Method method) {
        QueryHandler eventAnn = method.getDeclaredAnnotation(QueryHandler.class);
        if (eventAnn != null) {
            return queryMethodRegistry;
        }
        return null;
    }

}
