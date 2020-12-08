package com.monalisa.cqrsframework.api.commands;

import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.dto.Command;
import lombok.Data;

/**
 * Created on 2019-08-13.
 *
 * @author: jiyanbin
 */
@Data
public class UserLoginCmd extends Command<UserInfo> {

    private UserInfo userInfo;
    private String source="杭州";
}
