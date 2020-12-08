package com.monalisa.cqrsframework.exception;

/**
 * Base Exception is the parent of all exceptions
 *
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private IErrorCode errCode;

    public BaseException(String errMessage) {
        super(errMessage);
    }

    public BaseException(String errMessage, Throwable e) {
        super(errMessage, e);
    }

    public IErrorCode getErrCode() {
        return errCode;
    }

    public void setErrCode(IErrorCode errCode) {
        this.errCode = errCode;
    }

}
