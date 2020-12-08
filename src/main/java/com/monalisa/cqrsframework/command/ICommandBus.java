package com.monalisa.cqrsframework.command;


import com.monalisa.cqrsframework.dto.Command;

/**
 * CommandBus
 */
public interface ICommandBus {

    /**
     * Send command to CommandBus, then the command will be executed by CommandExecutor
     *
     * @param cmd or qry
     * @return <T>
     */
    <T> T send(Command cmd);
}
