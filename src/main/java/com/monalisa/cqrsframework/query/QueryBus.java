package com.monalisa.cqrsframework.query;

import com.monalisa.cqrsframework.dto.Query;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Just send Query to QueryBus,
 */
@Setter
@Component
public class QueryBus implements IQueryBus {

    @Autowired
    private QueryHub queryHub;

    @Override
    public <T> T send(Query query) {
        return queryHub.doQuery(query);
    }

    @Override
    public <T> T send(Query query, Class<T> resultType) {
        return queryHub.doQuery(query,resultType);
    }
}
