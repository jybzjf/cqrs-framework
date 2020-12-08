package com.monalisa.cqrsframework.client;

import com.monalisa.cqrsframework.api.commands.UserLoginCmd;
import com.monalisa.cqrsframework.api.commands.UserRegisterCmd;
import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.api.querys.UserInfosQuery;
import com.monalisa.cqrsframework.command.ICommandBus;
import com.monalisa.cqrsframework.query.IQueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2019-08-13.
 *
 * @author: jiyanbin
 */
@Component
public class UserClient {

    @Autowired
    private ICommandBus commandBus;

    @Autowired
    private IQueryBus queryBus;

    public UserInfo registe(UserInfo userInfo) {
        UserRegisterCmd cmd = new UserRegisterCmd();
        cmd.setUserInfo(userInfo);
        UserInfo response = commandBus.send(cmd);
        return response;
    }

    public UserInfo login(UserInfo userInfo) {
        UserLoginCmd cmd = new UserLoginCmd();
        cmd.setUserInfo(userInfo);
        UserInfo response = commandBus.send(cmd);
        return response;
    }

    public UserInfo query(UserInfo userInfo) {
        UserInfosQuery query = new UserInfosQuery();
        query.setIdCard(userInfo.getIdcard());
        query.setContext(userInfo);
        UserInfo response = queryBus.send(query);
        return response;
    }
}
