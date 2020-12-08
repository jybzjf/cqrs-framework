/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.command.PostCommandInterceptor;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.command.CommandHub;
import com.monalisa.cqrsframework.command.ICommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PostInterceptorRegister
 */
@Component
public class PostCommandInterceptorRegister implements IRegister {

    @Autowired
    private CommandHub commandHub;

    @Override
    public void doRegistration(Class<?> targetClz) {
        //处理被代理的bean，如Spring的动态代理，否则Spring动态代理后的targetClz将会失去代理增加的功能，如事务支持。
        /*ICommandInterceptor commandInterceptor = (ICommandInterceptor) ProxyTargetUtils
                .enhanceTargetForInterface((applicationContext.getBean(targetClz)),ICommandInterceptor.class);*/
        ICommandInterceptor commandInterceptor = (ICommandInterceptor) SpringApplicationContextHelper.getBean(targetClz);
        PostCommandInterceptor postCommandInterceptorAnn = targetClz.getDeclaredAnnotation(PostCommandInterceptor.class);
        Class<? extends Command>[] supportClasses = postCommandInterceptorAnn.commands();
        registerInterceptor(supportClasses, commandInterceptor);
    }

    private void registerInterceptor(Class<? extends Command>[] supportClasses, ICommandInterceptor commandInterceptor) {
        if (null == supportClasses || supportClasses.length == 0) {
            commandHub.registerGlobalPostInterceptors(commandInterceptor);
            return;
        }
        for (Class<? extends Command> supportClass : supportClasses) {
            commandHub.registerPostInterceptors(supportClass, commandInterceptor);
        }
    }

}
