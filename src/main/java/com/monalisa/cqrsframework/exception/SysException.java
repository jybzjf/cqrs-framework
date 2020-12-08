package com.monalisa.cqrsframework.exception;

/**
 * SysException
 */
public class SysException extends BaseException {

    private static final long serialVersionUID = 1L;

    public SysException(String errMessage) {
        super(errMessage);
        this.setErrCode(BasicErrorCode.S_UNKNOWN);
    }

    public SysException(IErrorCode errCode, String errMessage) {
        super(errMessage);
        this.setErrCode(errCode);
    }

    public SysException(String errMessage, Throwable e) {
        super(errMessage, e);
        this.setErrCode(BasicErrorCode.S_UNKNOWN);
    }
}
