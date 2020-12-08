package com.monalisa.cqrsframework.query;

import com.monalisa.cqrsframework.AbstractInvocation;
import com.monalisa.cqrsframework.dto.Query;
import lombok.Data;


/**
 * 命令执行上下文
 */
@Data
public class QueryInvocation extends AbstractInvocation<Query> {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "QueryInvocation \n{\n"+
                this.getTarget().getClass().getName()+"."+this.getMethod().getName()+":"+this.getMethod().getReturnType().getName()+"\n}";
    }
}
