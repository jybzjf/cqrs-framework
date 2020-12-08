/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.command.CommandInvocation;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.util.SpringApplicationContextHelper;
import com.monalisa.cqrsframework.command.CommandHub;
import com.monalisa.cqrsframework.exception.ComponentException;
import com.monalisa.cqrsframework.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;


/**
 * CommandRegister
 */

@Component
@DependsOn({"preCommandInterceptorRegister", "postCommandInterceptorRegister"})
public class CommandRegister implements IRegister {

    @Autowired
    private CommandHub commandHub;

    @Override
    public void doRegistration(Class<?> targetClz) {
        Class<? extends Command> commandClz = getCommandFromExecutor(targetClz);
        CommandInvocation commandInvocation = new CommandInvocation();
        //ApplicationContextHelper.getBean(CommandInvocation.class);
        //处理被代理的bean，如Spring的动态代理，否则Spring动态代理后的targetClz将会失去代理增加的功能，如事务支持。
        /*commandInvocation.setCommandExecutor((ICommandExecutor) ProxyTargetUtils
                .enhanceTargetForInterface(ApplicationContextHelper.getBean(targetClz),ICommandExecutor.class));*/
        //commandInvocation.setCommandExecutor((ICommandExecutor) ApplicationContextHelper.getBean(targetClz));
        //ICommandExecutor commandExecutor = (ICommandExecutor) ApplicationContextHelper.getBean(targetClz);
        //ICommandExecutor.class.getMethod("execute", Command.class);
        Method[] methods = targetClz.getMethods();
        Optional<Method> optionalMethod = Arrays.stream(methods).filter(method ->
                method.getName().equals(Constant.COMMAND_EXE_METHOD) &&
                        method.getParameterCount() == 1 &&
                        Command.class.isAssignableFrom(method.getParameterTypes()[0])).findFirst();
        if (optionalMethod.isPresent()) {
            commandInvocation.setMethod(optionalMethod.get());
            commandInvocation.setTarget(SpringApplicationContextHelper.getBean(targetClz));
        }else{
            throw new RuntimeException("Class <"+targetClz.getName()+">命令处理器的签名错误，方法名一定是execute，参数有且只有一个Command");
        }
        commandHub.register(commandClz, commandInvocation);
    }

    private Class<? extends Command> getCommandFromExecutor(Class<?> commandExecutorClz) {
        Method[] methods = commandExecutorClz.getDeclaredMethods();
        for (Method method : methods) {
            if (isExecuteMethod(method)) {
                Class commandClz = checkAndGetCommandParamType(method);
                commandHub.registerResponseRepository(commandClz, method.getReturnType());
                return (Class<? extends Command>) commandClz;
            }
        }
        throw new ComponentException(" There is no " + Constant.COMMAND_EXE_METHOD + "() in " + commandExecutorClz);
    }

    private boolean isExecuteMethod(Method method) {
        return Constant.COMMAND_EXE_METHOD.equals(method.getName()) && !method.isBridge();
    }

    protected Class checkAndGetCommandParamType(Method method) {
        Class<?>[] exeParams = method.getParameterTypes();
        if (exeParams.length == 0) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should at least have one parameter");
        }
        if (!Command.class.isAssignableFrom(exeParams[0])) {
            throw new ComponentException("Execute method in " + method.getDeclaringClass() + " should be the subClass of Command");
        }
        return exeParams[0];
    }

}
