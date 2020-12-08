package com.monalisa.cqrsframework.event;


/**
 * EventBus
 */
public interface IEventBus {

    /**
     * Send event to EventBus
     * 
     * @param event
     * @return Response
     */
    void fire(Object event);

    /**
     * Send event to EventBus
     *
     * @param event
     * @return Response
     */
    void asyncFire(Object event);

}
