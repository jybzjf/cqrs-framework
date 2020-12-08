/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

/**
 * Register Interface
 */
public interface IRegister {
    /**
     * 注册相应的处理器到容器中
     * @param targetClz
     */
    void doRegistration(Class<?> targetClz);
}
