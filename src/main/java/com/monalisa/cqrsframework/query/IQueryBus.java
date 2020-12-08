package com.monalisa.cqrsframework.query;


import com.monalisa.cqrsframework.dto.Query;

/**
 * queryBus
 */
public interface IQueryBus {

    /**
     * 发送一个查询，由查询处理器执行。
     * <p>
     * 如果此query对应的查询处理器有多个，则默认取第一个注册的执行，执行器的顺序是在容器启动时动态确定的，具有不确定性。
     * 如果一个Query只有一个对应的处理器，可以使用此方法。
     * 如果一个Query有多个处理器，请使用{@link this#send(Query, Class)}
     *
     * @param query or qry
     * @return Response
     */
    @Deprecated
    <T> T send(Query query);

    /**
     * query with return type {@code resultType}
     * @param query
     * @param resultType
     * @param <T>
     * @return
     */
    @Deprecated
    <T> T send(Query query,Class<T> resultType);

    /**
     * 执行查询
     * @param query 查询对象
     * @param <T>   类型
     * @return      查询结果
     */
    default <T> T query(Query query){
        return send(query);
    }

    /**
     * 执行查询
     * @param query      查询对象
     * @param resultType 返回值的类类型
     * @param <T>        类型
     * @return           查询结果
     */
    default <T> T query(Query query,Class<T> resultType){
        return send(query, resultType);
    }
}
