package com.monalisa.cqrsframework.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.monalisa.cqrsframework.boot.ProxyTargetUtils;
import com.monalisa.cqrsframework.dto.Query;
import com.monalisa.cqrsframework.dto.Response;
import com.monalisa.cqrsframework.exception.BasicErrorCode;
import com.monalisa.cqrsframework.exception.BizException;
import com.monalisa.cqrsframework.exception.ComponentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Query Hub holds all the important information about Query
 */
@SuppressWarnings("rawtypes")
@Component
@Slf4j
public class QueryHub {

    /**
     * key is QueryClz
     */
    private ListMultimap<Class, IQueryInterceptor> preInterceptors = LinkedListMultimap.create();
    /**
     * key is QueryClz
     */
    private ListMultimap<Class, IQueryInterceptor> postInterceptors = LinkedListMultimap.create();
    /**
     * 全局通用的PreInterceptors
     */
    private List<IQueryInterceptor> globalPreInterceptors = new ArrayList<>();
    /**
     * 全局通用的PostInterceptors
     */
    private List<IQueryInterceptor> globalPostInterceptors = new ArrayList<>();
    /**
     * key is QueryClz
     */
    //private Map<Class, QueryInvocation> queryRepository = new HashMap<>();

    private ListMultimap<Class, QueryInvocation> queryRepository = ArrayListMultimap.create();

    /**
     * This Repository is used for return right response type on exception scenarios
     * key is QueryClz
     * value is ResponseClz
     */
    //private Map<Class, Class> responseRepository = new HashMap<>();


    /**
     * 注册事件
     *
     * @param queryClz
     * @param executor
     */
    public void register(Class<? extends Query> queryClz, QueryInvocation executor) {
        if(queryRepository.containsValue(executor)){
            throw new ComponentException("查询对象<"+queryClz.getName()+">对应的执行器<"+executor.toString()+">已注册");
        }
        queryRepository.put(queryClz, executor);
    }

    /*public void registerResponseRepository(Class queryClz, Class returnType) {
        responseRepository.put(queryClz, returnType);
    }*/


    public void registerPreInterceptors(Class<? extends Query> supportClass, IQueryInterceptor eventInterceptor) {
        preInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerPostInterceptors(Class<? extends Query> supportClass, IQueryInterceptor eventInterceptor) {
        postInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerGlobalPreInterceptors(IQueryInterceptor eventInterceptor) {
        globalPreInterceptors.add(eventInterceptor);
    }

    public void registerGlobalPostInterceptors(IQueryInterceptor eventInterceptor) {
        globalPostInterceptors.add(eventInterceptor);
    }

    /*public <T> Response<T> getResponseInstance(Query cmd) {
        Class responseClz = this.responseRepository.get(cmd.getClass());
        try {
            return (Response<T>) responseClz.newInstance();
        } catch (Exception e) {
            return new Response<>();
        }
    }*/

    public <T> T doQuery(Query query) {
        Response<T> response = Response.<T>buildSuccess();
        try {
            executePreInterceptors(query);
            QueryInvocation queryInvocation = getQueryInvocation(query.getClass());
            T result = queryInvocation.invoke(query);
            response.setData(result);

        } catch (Exception e) {
            /*response = getResponseInstance(query);
            response.setSuccess(false);
            ExceptionHandlerFactory.getExceptionHandler().handleException(query, response, e);*/
            log.error("Handle query <" + query.getClass().getName() + "> content is <" + query.getContext() + "> error!", e);
            response = Response.<T>buildFailure(BasicErrorCode.B_COMMON_ERROR.getErrCode(), BasicErrorCode.B_COMMON_ERROR.getErrDesc());
            throw new BizException("执行查询错误!", e);
        } finally {
            //make sure post interceptors performs even though exception happens
            executePostIntercept(query, response);
        }
        return response.getData();
    }

    public <T> T doQuery(Query query, Class<T> clz) {
        Class queryClass = query.getClass();
        List<QueryInvocation> queryInvocations = queryRepository.get(query.getClass());
        if (queryInvocations == null || queryInvocations.isEmpty()) {
            throw new ComponentException(queryClass + " is not registered in QueryHub, please register first");
        }
        QueryInvocation queryInvocation = queryInvocations.stream()
                .filter(invocation -> invocation.getMethod().getReturnType().equals(clz))
                .findFirst()
                .orElseThrow(() -> new ComponentException(queryClass + " is not registered in QueryHub, please register first"));
        Response<T> response = Response.<T>buildSuccess();
        try {
            T result = queryInvocation.invoke(query);
            response.setData(result);
        } catch (Exception e) {
            log.error("Handle query <" + query.getClass().getName() + "> content is <" + query.getContext() + "> error!", e);
            throw new BizException("执行查询错误!", e);
        } finally {
            //make sure post interceptors performs even though exception happens
            executePostIntercept(query, response);
        }
        return response.getData();
    }

    private void executePreInterceptors(Query query) {
        //先执行全局的
        for (IQueryInterceptor globalPreInterceptor : globalPreInterceptors) {
            if (understandQuery(globalPreInterceptor, query.getClass())) {
                globalPreInterceptor.preIntercept(query);
            }
        }
        //再执行query特有的
        Class<?> superClass = query.getClass();
        while (Query.class.isAssignableFrom(superClass)) {
            List<IQueryInterceptor> preInterceptors = this.preInterceptors.get(superClass);
            for (IQueryInterceptor preInterceptor : preInterceptors) {
                preInterceptor.preIntercept(query);
            }
            superClass = superClass.getSuperclass();
        }
    }

    private <T> void executePostIntercept(Query query, Response<T> response) {
        try {
            //先执行query特有的
            Class<?> superClass = query.getClass();
            while (Query.class.isAssignableFrom(superClass)) {
                List<IQueryInterceptor> postInterceptors = this.postInterceptors.get(superClass);
                for (IQueryInterceptor postInterceptor : postInterceptors) {
                    postInterceptor.postIntercept(query, response);
                }
                superClass = superClass.getSuperclass();
            }
            //再执行全局的
            for (IQueryInterceptor globalPostInterceptor : globalPostInterceptors) {
                if (understandQuery(globalPostInterceptor, query.getClass())) {
                    globalPostInterceptor.postIntercept(query, response);
                }
            }

        } catch (Exception e) {
            log.error("postInterceptor error:" + e.getMessage(), e);
        }
    }


    private boolean understandQuery(IQueryInterceptor interceptor, Class queryExeClz) {
        IQueryInterceptor target = (IQueryInterceptor) ProxyTargetUtils.getTarget(interceptor);
        Type[] genType = target.getClass().getGenericInterfaces();
        for (Type type : genType) {
            ParameterizedTypeImpl paramType = (ParameterizedTypeImpl) type;
            if (paramType.getRawType().equals(IQueryInterceptor.class)) {
                return ((Class) (paramType.getActualTypeArguments()[0])).isAssignableFrom(queryExeClz);
            }
        }
        return false;
    }

    private QueryInvocation getQueryInvocation(Class queryClass) {
        List<QueryInvocation> queryInvocations = queryRepository.get(queryClass);
        if (queryInvocations == null || queryInvocations.isEmpty()) {
            throw new ComponentException(queryClass + " is not registered in QueryHub, please register first");
        }
        return queryInvocations.get(0);
    }

}
