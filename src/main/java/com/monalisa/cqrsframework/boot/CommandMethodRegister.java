/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.command.CommandHub;
import com.monalisa.cqrsframework.command.CommandInvocation;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * CommandRegister
 */

@Component("commandMethodRegister")
public class CommandMethodRegister extends CommandRegister implements IMethodRegistry {

    @Autowired
    private CommandHub commandHub;

    @Override
    public void doRegistration(Class<?> targetClz, Method method) {
        Class<? extends Command> commandClz = checkAndGetCommandParamType(method);
        CommandInvocation commandInvocation = new CommandInvocation();
        //ApplicationContextHelper.getBean(CommandInvocation.class);

        commandInvocation.setMethod(method);
        commandInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));

        commandHub.register(commandClz, commandInvocation);
    }
}
