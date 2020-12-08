package com.monalisa.cqrsframework.exception;


import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultExceptionHandler
 *
 */
public class DefaultExceptionHandler implements IExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public static DefaultExceptionHandler singleton = new DefaultExceptionHandler();

    @Override
    public void handleException(Command cmd, Response response, Exception exception) {
        buildResponse(response, exception);
        printLog(cmd, response, exception);
    }

    private void printLog(Command cmd, Response response, Exception exception) {
        if (exception instanceof BizException) {
            //biz exception is expected, only warn it
            logger.warn(buildErrorMsg(cmd, response));
        } else {
            //sys exception should be monitored, and pay attention to it
            logger.error(buildErrorMsg(cmd, response), exception);
        }
    }

    private String buildErrorMsg(Command cmd, Response response) {
        return "Process [" + cmd + "] failed, errorCode: "
                + response.getErrCode() + " errorMsg:"
                + response.getErrMessage();
    }

    private void buildResponse(Response response, Exception exception) {
        if (exception instanceof BaseException) {
            IErrorCode errCode = ((BaseException) exception).getErrCode();
            response.setErrCode(errCode.getErrCode());
        } else {
            response.setErrCode(BasicErrorCode.S_UNKNOWN.getErrCode());
        }
        response.setErrMessage(exception.getMessage());
        response.setSuccess(false);
    }
}
