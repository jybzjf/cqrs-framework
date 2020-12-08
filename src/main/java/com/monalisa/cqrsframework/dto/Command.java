package com.monalisa.cqrsframework.dto;


/**
 * Command stands for a request from Client.
 * According CommandExecutor will help to handle the business logic. This is a classic Command Pattern
 *
 * @author fulan.zjf 2017年10月27日 下午12:28:24
 */
public abstract class Command<T> extends DTO {

    private static final long serialVersionUID = 1L;

    private T context;

    public T getContext() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }
}
