package com.monalisa.cqrsframework.dto;

import lombok.Data;

/**
 * Response to caller
 */
@Data
public class Response<T> extends DTO {

    private static final long serialVersionUID = 1L;

    private boolean isSuccess;

    private String errCode;

    private String errMessage;

    private T data;


    @Override
    public String toString() {
        return "Response [isSuccess=" + isSuccess + ", errCode=" + errCode + ", errMessage=" + errMessage + "]";
    }

    public static Response buildFailure(String errCode, String errMessage) {
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    public static Response buildSuccess() {
        Response response = new Response();
        response.setSuccess(true);
        return response;
    }

}
