package com.monalisa.cqrsframework.query;

import com.monalisa.cqrsframework.dto.Query;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreQueryInterceptor {

    Class<? extends Query>[] querys() default {};

}
