package com.monalisa.cqrsframework.command;

import com.monalisa.cqrsframework.dto.Command;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreCommandInterceptor {

    Class<? extends Command>[] commands() default {};

}
