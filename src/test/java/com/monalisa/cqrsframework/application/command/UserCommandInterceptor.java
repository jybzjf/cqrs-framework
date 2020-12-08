package com.monalisa.cqrsframework.application.command;

import com.monalisa.cqrsframework.api.commands.UserLoginCmd;
import com.monalisa.cqrsframework.api.commands.UserRegisterCmd;
import com.monalisa.cqrsframework.command.ICommandInterceptor;
import com.monalisa.cqrsframework.command.PostCommandInterceptor;
import com.monalisa.cqrsframework.command.PreCommandInterceptor;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.dto.Response;

/**
 * Created on 2019-08-10.
 *
 * @author: jiyanbin
 */
@PreCommandInterceptor(commands = {UserRegisterCmd.class, UserLoginCmd.class})
@PostCommandInterceptor(commands = {UserLoginCmd.class})
public class UserCommandInterceptor implements ICommandInterceptor {

    @Override
    public void preIntercept(Command command) {
        System.out.println("pre UserCommandInterceptor " + command);
    }

    @Override
    public void postIntercept(Command command, Response response) {
        System.out.println("post UserCommandInterceptor " + command);
    }
}
