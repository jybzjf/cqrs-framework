package com.monalisa.cqrsframework.application.event;


import com.monalisa.cqrsframework.event.EventHandler;
import org.springframework.stereotype.Component;

/**
 * AddCustomerCmd
 *
 * @author Frank Zhang 2018-01-06 7:28 PM
 */
@Component
public class UserRegistedEventHandler {

    @EventHandler
    public void execute(UserRegistedEvent userRegistedEvent) {
        System.out.println("==UserRegistedEventHandler handle "+ userRegistedEvent);
    }

    @EventHandler
    public void execute2(UserRegistedEvent userRegistedEvent) {
        System.out.println("==UserRegistedEventHandler handle 2 "+ userRegistedEvent);
    }
}
