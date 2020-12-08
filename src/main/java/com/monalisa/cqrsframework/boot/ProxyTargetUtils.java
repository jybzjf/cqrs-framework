package com.monalisa.cqrsframework.boot;

/**
 * Created on 2019-08-12.
 *
 * @author: jiyanbin
 */

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理处理工具类，处理JDK及spring的动态代理(JDK或CGLIB)
 */
public class ProxyTargetUtils {
    /**
     * 获取 目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    public static Object getTarget(Object proxy) {
        try {
            if (!AopUtils.isAopProxy(proxy) && !Proxy.isProxyClass(proxy.getClass())) {
                return proxy;
            }
            if (Proxy.isProxyClass(proxy.getClass())) {
                proxy = getJdkDynamicProxyTargetObject(proxy);
            }
            else if (AopUtils.isJdkDynamicProxy(proxy)) {
                proxy = getSpringJdkDynamicProxyTargetObject(proxy);
            } else {
                proxy = getCglibProxyTargetObject(proxy);
            }
            return getTarget(proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 只支持接口代理
     *
     * @param target
     * @param wrapperClazz
     * @return
     */
    public static Object enhanceTargetForInterface(Object target, Class<?> wrapperClazz) {
        //todo 支持类代理
        if (wrapperClazz.isInterface()) {
            InvocationHandler invocationHandler = new JDKInvocationHandler(target);
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{wrapperClazz}, invocationHandler);
        }
        throw new RuntimeException("暂不支持类代理。。。。。。。");
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }

    private static Object getSpringJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        return ((JDKInvocationHandler) h.get(proxy)).getTarget();
    }

    private static class JDKInvocationHandler implements InvocationHandler {
        /**
         * 目标对象
         */
        private final Object target;

        public JDKInvocationHandler(Object target) {
            this.target = target;
        }

        public Object getTarget() {
            return target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(target, args);

            } catch (Throwable throwable) {
                throw throwable;
            }
        }
    }
}
