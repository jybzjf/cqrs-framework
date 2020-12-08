package com.monalisa.cqrsframework.event;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * @author jiyanbin
 */
@Data
public class EventInvocation {

    private static Logger logger = LoggerFactory.getLogger(EventInvocation.class);

    //private IEventHandler eventHandler;

    private Method method;

    private Object target;

    public void invoke(Object event){
        //eventHandler.execute(event);
        try {
            method.invoke(target, event);
        } catch (Exception e) {
            logger.error("Handler event <"+event.toString()+"> error!",e);
        }
    }
}
