package com.monalisa.cqrsframework.application.query;

import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.api.querys.UserInfosQuery;
import com.monalisa.cqrsframework.query.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created on 2019-08-14.
 *
 * @author: jiyanbin
 */
@Component
public class UserQueryService {
    private Logger logger = LoggerFactory.getLogger(UserQueryService.class);
    @QueryHandler
    public UserInfo query(UserInfosQuery query) {
        logger.info("Start processing query:" + query);
        logger.info("End processing query:" + query);
        return new UserInfo(query.getIdCard(),query.getContext().getName());
    }

}
