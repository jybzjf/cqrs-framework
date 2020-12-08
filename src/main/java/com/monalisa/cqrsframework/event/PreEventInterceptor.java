package com.monalisa.cqrsframework.event;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreEventInterceptor {

    Class<?>[] events() default {};

}
