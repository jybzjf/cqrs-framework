package com.monalisa.cqrsframework.application.event;


import lombok.Data;

/**
 * AddCustomerCmd
 *
 * @author Frank Zhang 2018-01-06 7:28 PM
 */
@Data
public class UserRegistedEvent{

    private String idcard;
    @Override
    public String toString() {
        return "UserRegistedEvent{}";
    }
}
