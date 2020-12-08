package com.monalisa.cqrsframework.command;

import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.dto.Response;

/**
 * Interceptor will do AOP processing before or after Command Execution
 *
 */
public interface ICommandInterceptor<T extends Command> {

    /**
     * Pre-processing before command execution
     *
     * @param command
     */
    default public void preIntercept(T command) {
    }

    /**
     * Post-processing after command execution
     *
     * @param command
     * @param response, Note that response could be null, check it before use
     */
    default public void postIntercept(T command, Response response) {
    }
}
