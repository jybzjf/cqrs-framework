package com.monalisa.cqrsframework.command;

import com.monalisa.cqrsframework.dto.Command;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Just send Command to CommandBus,
 */
@Setter
@Component
public class CommandBus implements ICommandBus {

    @Autowired
    private CommandHub commandHub;

    @Override
    public <T> T send(Command cmd) {
        return commandHub.doCommand(cmd);
    }

    /*@Override
    public Response send(Command cmd, Class<? extends ICommandExecutor> executorClz) {
        CommandInvocation commandInvocation = commandHub.getCommandInvocation(cmd.getClass());
        if (!isEquals(executorClz, commandInvocation)) {
            throw new IllegalArgumentException(executorClz + " is not the same with " + commandInvocation.getTarget().getClass());
        }
        return send(cmd);
    }

    private boolean isEquals(Class<? extends ICommandExecutor> executorClz, CommandInvocation commandInvocation) {
        return executorClz.equals(ProxyTargetUtils.getTarget(commandInvocation.getTarget()).getClass());
    }*/

}
