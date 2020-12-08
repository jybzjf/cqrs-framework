package com.monalisa.cqrsframework.application.command;


import com.monalisa.cqrsframework.api.commands.UserLoginCmd;
import com.monalisa.cqrsframework.api.commands.UserRegisterCmd;
import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.application.event.UserRegistedEvent;
import com.monalisa.cqrsframework.command.CommandHandler;
import com.monalisa.cqrsframework.event.IEventBus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AddCustomerCmd
 *
 * @author Frank Zhang 2018-01-06 7:28 PM
 */
@Data
@Component
public class UserAppService {
    private Logger logger = LoggerFactory.getLogger(UserAppService.class);

    @Autowired
    private IEventBus eventBus;

    @CommandHandler
    public UserInfo register(UserRegisterCmd cmd) {
        logger.info("Start processing command:" + cmd);
        logger.info("End processing command:" + cmd);
        UserRegistedEvent event = new UserRegistedEvent();
        event.setIdcard(cmd.getUserInfo().getIdcard());
        eventBus.fire(event);
        return cmd.getUserInfo();
    }

    @CommandHandler
    public UserInfo login(UserLoginCmd cmd) {
        logger.info("Start processing command:" + cmd);
        logger.info("End processing command:" + cmd);
        return cmd.getUserInfo();
    }
}
