package com.monalisa.cqrsframework.query;

import com.monalisa.cqrsframework.dto.Query;
import com.monalisa.cqrsframework.dto.Response;

/**
 * Interceptor will do AOP processing before or after Query Execution
 *
 */
public interface IQueryInterceptor<T extends Query> {

    /**
     * Pre-processing before query execution
     *
     * @param query
     */
    default public void preIntercept(T query) {
    }

    /**
     * Post-processing after query execution
     *
     * @param query
     * @param response, Note that response could be null, check it before use
     */
    default public void postIntercept(T query, Response response) {
    }
}
