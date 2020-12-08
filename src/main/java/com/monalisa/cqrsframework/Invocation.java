package com.monalisa.cqrsframework;

/**
 * Created on 2019-08-14.
 *
 * @author: jiyanbin
 */

public interface Invocation<T> {

    /**
     * 执行上下文
     * @param invocation
     * @param <R>
     * @return
     * @throws Exception
     */
    <R> R invoke(T invocation) throws Exception;
}
