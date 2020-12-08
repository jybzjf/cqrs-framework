package com.monalisa.cqrsframework.application.command;

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
@PreCommandInterceptor
@PostCommandInterceptor
public class GlobalCommandInterceptor implements ICommandInterceptor<Command> {

    @Override
    public void preIntercept(Command command) {
        System.out.println("pre GlobalCommandInterceptor " + command);
    }

    @Override
    public void postIntercept(Command command, Response response) {
        System.out.println("post GlobalCommandInterceptor " + command);
    }
}
