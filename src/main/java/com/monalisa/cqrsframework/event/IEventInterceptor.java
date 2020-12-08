package com.monalisa.cqrsframework.event;

/**
 * Interceptor will do AOP processing before or after Event Execution
 *
 */
public interface IEventInterceptor<T> {

    /**
     * Pre-processing before event execution
     *
     * @param event
     */
    default public void preIntercept(T event) {
    }

    /**
     * Post-processing after event execution
     *
     * @param event
     */
    default public void postIntercept(T event) {
    }

}
