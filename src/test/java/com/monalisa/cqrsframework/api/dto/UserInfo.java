package com.monalisa.cqrsframework.api.dto;

import lombok.Data;

/**
 * Created on 2019-08-13.
 *
 * @author: jiyanbin
 */
@Data
public class UserInfo {


    public UserInfo() {
    }

    public UserInfo(String idcard, String name) {
        this.idcard = idcard;
        this.name = name;
    }

    private String idcard;
    private String name;
}
