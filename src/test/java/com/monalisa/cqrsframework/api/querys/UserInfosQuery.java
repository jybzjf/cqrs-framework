package com.monalisa.cqrsframework.api.querys;
import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.dto.Query;
import lombok.Data;

/**
 * Created on 2019-08-13.
 *
 * @author: jiyanbin
 */
@Data
public class UserInfosQuery extends Query<UserInfo> {

    private String idCard;
    private String source="杭州";
}
