package com.monalisa.cqrsframework.exception;

/**
 * Application Exceptions can be divided into Biz Exception and Sys Exception.
 * <p>
 * Business Exception is not retry-able, while System Exception could be instantaneous and retry-able.
 * <p>
 * So i suggest to define 2 types of ErrorCode, the format is TYPE_SCENARIO_REASON
 * <p>
 * TYPE: B_  Stands for Business
 * TYPE: S_  Stands for System
 * <p>
 */
public enum BasicErrorCode implements IErrorCode {

    /**
     * Business Exception
     * <p>
     * You can extend it by implementing ErrorCodeI in your Application
     * For example:
     *
     * <pre class="code">
     * B_CUSTOMER_NameIsNull("B_CUSTOMER_NameIsNull","客户姓名不能为空")
     * B_CUSTOMER_NameAlreadyExist("B_CUSTOMER_NameAlreadyExist","客户姓名已经存在")
     * </pre>
     */
    B_COMMON_ERROR("B_COMMON_ERROR", "通用的业务逻辑错误"),

    /**
     * System Exception
     * You can extend it by implementing ErrorCodeI in your Application
     */
    S_COLA_ERROR("S_COLA_ERROR", "扩展点框架错误"),
    S_NPE("S_NPE", "空指针错误"),
    S_UNKNOWN("S_UNKNOWN", "未知的系统错误");

    private String errCode;
    private String errDesc;

    private BasicErrorCode(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public String getErrDesc() {
        return errDesc;
    }
}
