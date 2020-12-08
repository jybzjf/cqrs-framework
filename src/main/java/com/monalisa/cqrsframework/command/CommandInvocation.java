package com.monalisa.cqrsframework.command;

import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.AbstractInvocation;


/**
 * 命令执行上下文
 */
public class CommandInvocation extends AbstractInvocation<Command> {

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
        return "CommandInvocation \n{\n"+
                this.getTarget().getClass().getName()+"."+this.getMethod().getName()+":"+this.getMethod().getReturnType().getName()+"\n}";
    }
}
