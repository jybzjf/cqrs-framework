package com.monalisa.cqrsframework;

import com.monalisa.cqrsframework.boot.Bootstrap;
import com.monalisa.cqrsframework.command.CommandBus;
import com.monalisa.cqrsframework.command.CommandHub;
import com.monalisa.cqrsframework.command.ICommandBus;
import com.monalisa.cqrsframework.event.EventBus;
import com.monalisa.cqrsframework.event.EventHub;
import com.monalisa.cqrsframework.event.IEventBus;
import com.monalisa.cqrsframework.query.IQueryBus;
import com.monalisa.cqrsframework.query.QueryBus;
import com.monalisa.cqrsframework.query.QueryHub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * TestConfig
 *
 * @author Frank Zhang 2018-01-06 7:57 AM
 */
@Configuration
@ComponentScan(value = {"com.monalisa"})
//@PropertySource(value = {"/sample.properties"})
public class TestConfig {

    @Bean(initMethod = "init")
    public Bootstrap bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        List<String> packagesToScan = new ArrayList<>();
        packagesToScan.add("com.souche.cqrs");
        bootstrap.setPackages(packagesToScan);
        return bootstrap;
    }

    @Bean
    public ICommandBus commandBus(CommandHub commandHub){
        CommandBus commandBus = new CommandBus();
        commandBus.setCommandHub(commandHub);
        return commandBus;
    }

    @Bean
    public IQueryBus queryBus(QueryHub queryHub){
        QueryBus queryBus = new QueryBus();
        queryBus.setQueryHub(queryHub);
        return queryBus;
    }

    @Bean
    public IEventBus eventBus(EventHub eventHub){
        EventBus eventBus = new EventBus();
        eventBus.setEventHub(eventHub);
        return eventBus;
    }

}
