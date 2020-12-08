package com.monalisa.cqrsframework.exception;


import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.dto.Response;

/**
 * ExceptionHandlerI provide a backdoor that Application can override the default Exception handling
 * @author jiyanbin
 */
public interface IExceptionHandler {

    /**
     * 处理异常
     * @param cmd       命令
     * @param response  返回值
     * @param exception 异常
     */
    void handleException(Command cmd, Response response, Exception exception);
}
