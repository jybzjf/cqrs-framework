package com.monalisa.cqrsframework.exception;

/**
 * Extends your error codes in your App by implements this Interface.
 * @author jiyanbin
 */
public interface IErrorCode {

    /**
     * 返回错误码
     * @return
     */
    public String getErrCode();

    /**
     * 返回错误描述
     * @return
     */
    public String getErrDesc();

}
