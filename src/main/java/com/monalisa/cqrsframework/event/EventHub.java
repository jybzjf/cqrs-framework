package com.monalisa.cqrsframework.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.monalisa.cqrsframework.boot.ProxyTargetUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件控制中枢
 *
 * @author jiyanbin
 */
@SuppressWarnings("rawtypes")
@Component
@Slf4j
public class EventHub {

    /**
     * key is EventClz
     */
    private ListMultimap<Class, IEventInterceptor> preInterceptors = LinkedListMultimap.create();
    /**
     * key is EventClz
     */
    private ListMultimap<Class, IEventInterceptor> postInterceptors = LinkedListMultimap.create();

    /**
     * 全局通用的PreInterceptors
     */

    private List<IEventInterceptor> globalPreInterceptors = new ArrayList<>();
    /**
     * 全局通用的PostInterceptors
     */
    private List<IEventInterceptor> globalPostInterceptors = new ArrayList<>();
    /**
     * one event could have multiple event handlers
     */

    private ListMultimap<Class, EventInvocation> eventRepository = ArrayListMultimap.create();

    private List<EventInvocation> getEventHandler(Class<?> eventClass) {
        List<EventInvocation> eventInvocations = findHandler(eventClass);
        if (eventInvocations == null || eventInvocations.isEmpty()) {
            //throw new ComponentException(eventClass + " is not registered in eventHub, please register first");
            log.warn("====There is no event handler for event <"+eventClass.getName()+", if necessary,please register first! >====");
        }
        return eventInvocations;
    }

    public void registerPreInterceptors(Class<?> supportClass, IEventInterceptor eventInterceptor) {
        preInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerPostInterceptors(Class<?> supportClass, IEventInterceptor eventInterceptor) {
        postInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerGlobalPreInterceptors(IEventInterceptor eventInterceptor) {
        globalPreInterceptors.add(eventInterceptor);
    }

    public void registerGlobalPostInterceptors(IEventInterceptor eventInterceptor) {
        globalPostInterceptors.add(eventInterceptor);
    }

    /**
     * 注册事件
     *
     * @param eventClz
     * @param executor
     */
    public void register(Class<?> eventClz, EventInvocation executor) {
        eventRepository.put(eventClz, executor);
    }

    public void handleEvent(Object event) {
        executePreIntercept(event);
        getEventHandler(event.getClass()).stream().forEach(p -> {
            p.invoke(event);
        });
        executePostIntercept(event);
    }

    private List<EventInvocation> findHandler(Class<?> eventClass) {
        return eventRepository.get(eventClass);
    }

    private void executePostIntercept(Object event) {
        try {
            //先执行Event特有的
            Class<?> clazz = event.getClass();
            //while (Event.class.isAssignableFrom(superClass)) {
            do {
                List<IEventInterceptor> postInterceptors = this.postInterceptors.get(clazz);
                for (IEventInterceptor postInterceptor : postInterceptors) {
                    postInterceptor.postIntercept(event);
                }
                clazz = clazz.getSuperclass();
            } while (clazz != null);
            //再执行全局的
            for (IEventInterceptor globalPostInterceptor : this.globalPostInterceptors) {
                if (understandEvent(globalPostInterceptor, event.getClass())) {
                    globalPostInterceptor.postIntercept(event);
                }
            }
        } catch (Exception e) {
            log.error("postInterceptor error:" + e.getMessage(), e);
        }
    }

    private void executePreIntercept(Object event) {
        //先执行全局的
        for (IEventInterceptor globalPreInterceptor : this.globalPreInterceptors) {
            if (understandEvent(globalPreInterceptor, event.getClass())) {
                globalPreInterceptor.preIntercept(event);
            }
        }
        //执行Event特有的
        Class<?> clazz = event.getClass();
        //while (Event.class.isAssignableFrom(superClass)) {
        do {
            List<IEventInterceptor> preInterceptors = this.preInterceptors.get(clazz);
            for (IEventInterceptor preInterceptor : preInterceptors) {
                preInterceptor.preIntercept(event);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
    }

    private boolean understandEvent(IEventInterceptor interceptor, Class eventClz) {
        IEventInterceptor target = (IEventInterceptor) ProxyTargetUtils.getTarget(interceptor);
        Type[] genType = target.getClass().getGenericInterfaces();
        for (Type type : genType) {
            ParameterizedTypeImpl paramType = (ParameterizedTypeImpl) type;
            if (paramType.getRawType().equals(IEventInterceptor.class)) {
                return ((Class) (paramType.getActualTypeArguments()[0])).isAssignableFrom(eventClz);
            }
        }
        return false;
    }

}
