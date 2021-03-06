package com.monalisa.cqrsframework.event;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Component
public @interface EventHandler {
}
