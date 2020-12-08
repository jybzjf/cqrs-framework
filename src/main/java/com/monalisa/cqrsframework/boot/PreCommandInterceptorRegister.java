/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.command.CommandHub;
import com.monalisa.cqrsframework.command.ICommandInterceptor;
import com.monalisa.cqrsframework.command.PreCommandInterceptor;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PreInterceptorRegister
 */
@Component
public class PreCommandInterceptorRegister implements IRegister{

    @Autowired
    private CommandHub commandHub;

    @Override
    public void doRegistration(Class<?> targetClz) {
        //处理被代理的bean，如Spring的动态代理，否则Spring动态代理后的targetClz将会失去代理增加的功能，如事务支持。
        /*ICommandInterceptor commandInterceptor = (ICommandInterceptor) ProxyTargetUtils
                .enhanceTargetForInterface(applicationContext.getBean(targetClz),ICommandInterceptor.class);*/
        ICommandInterceptor commandInterceptor = (ICommandInterceptor) SpringApplicationContextHelper.getBean(targetClz);
                PreCommandInterceptor preCommandInterceptorAnn = targetClz.getDeclaredAnnotation(PreCommandInterceptor.class);
        Class<? extends Command>[] supportClasses = preCommandInterceptorAnn.commands();
        registerInterceptor(supportClasses, commandInterceptor);
    }

    private void registerInterceptor(Class<? extends Command>[] supportClasses, ICommandInterceptor commandInterceptor) {
        if (null == supportClasses || supportClasses.length == 0) {
            commandHub.registerGlobalPreInterceptors(commandInterceptor);
            return;
        }
        for (Class<? extends Command> supportClass : supportClasses) {
            commandHub.registerPreInterceptors(supportClass, commandInterceptor);
        }
    }
}
