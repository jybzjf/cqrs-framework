package com.monalisa.cqrsframework.exception;

/**
 * COLA framework Exception
 *
 */
public class ComponentException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ComponentException(String errMessage) {
        super(errMessage);
        this.setErrCode(BasicErrorCode.S_COLA_ERROR);
    }

    public ComponentException(String errMessage, Throwable e) {
        super(errMessage, e);
        this.setErrCode(BasicErrorCode.S_COLA_ERROR);
    }
}