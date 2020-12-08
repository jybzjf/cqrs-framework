package com.monalisa.cqrsframework;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * 命令执行上下文
 */
@Data
public class AbstractInvocation<T> implements Invocation<T>{

    private Method method;

    private Object target;

    @Override
    public <R> R invoke(T invocation) throws Exception {
        /*if (method.getReturnType() != null && !"void".equals(method.getReturnType().getName())) {
            (R)method.invoke(target, invocation);
        } else {
            method.invoke(target, invocation);
        }*/

        return (R)method.invoke(target, invocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractInvocation)) {
            return false;
        }
        AbstractInvocation<?> that = (AbstractInvocation<?>) o;
        Class<?>[] exeParams = getMethod().getParameterTypes();
        Class<?>[] thatParams = that.getMethod().getParameterTypes();

        return getMethod().getReturnType().equals(that.getMethod().getReturnType()) &&
                exeParams[0].equals(thatParams[0]) &&
                getTarget().getClass().equals(that.getTarget().getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getTarget().getClass());
    }
}
