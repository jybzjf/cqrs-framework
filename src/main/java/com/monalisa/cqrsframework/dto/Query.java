package com.monalisa.cqrsframework.dto;

/**
 * Query is a special Command which will directly call DataTunnel for data objects
 *
 * @author fulan.zjf 2017年10月22日 下午7:26:49
 */
public abstract class Query<T> extends Command<T> {

    private static final long serialVersionUID = 1L;

}
