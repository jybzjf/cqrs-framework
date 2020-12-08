/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.monalisa.cqrsframework.boot;

import com.monalisa.cqrsframework.exception.ComponentException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <B>应用的核心引导启动类</B>
 * <p>
 * 负责扫描在applicationContext.xml中配置的packages. 获取到CommandExecutors, intercepters, EventHandler等
 * 交给各个注册器进行注册。
 */
public class Bootstrap {
    @Getter
    @Setter
    private List<String> packages;
    private ClassPathScanHandler handler;

    @Autowired
    private RegisterFactory registerFactory;


    public void init() {
        Set<Class<?>> classSet = scanConfiguredPackages();
        registerBeans(classSet);
    }

    /**
     * @param classSet
     */
    private void registerBeans(Set<Class<?>> classSet) {
        for (Class<?> targetClz : classSet) {
            List<IRegister> register = registerFactory.getRegister(targetClz);
            if (null != register && !register.isEmpty()) {
                register.forEach(r -> r.doRegistration(targetClz));
            }
            Method[] methods = targetClz.getMethods();
            for (Method method : methods) {
                IMethodRegistry methodRegistry = registerFactory.getCommandMethodRegister(method);
                if (methodRegistry != null) {
                    methodRegistry.doRegistration(targetClz, method);
                }
                methodRegistry = registerFactory.getEventMethodRegister(method);
                if (methodRegistry != null) {
                    methodRegistry.doRegistration(targetClz, method);
                }
                methodRegistry = registerFactory.getQueryMethodRegister(method);
                if (methodRegistry != null) {
                    methodRegistry.doRegistration(targetClz, method);
                }
            }
        }
    }

    /**
     * Scan the packages configured in Spring xml
     *
     * @return
     */
    private Set<Class<?>> scanConfiguredPackages() {
        if (packages == null) {
            throw new ComponentException("Command packages is not specified");
        }

        String[] pkgs = new String[packages.size()];
        handler = new ClassPathScanHandler(packages.toArray(pkgs));

        Set<Class<?>> classSet = new TreeSet<>(new ClassNameComparator());
        for (String pakName : packages) {
            classSet.addAll(handler.getPackageAllClasses(pakName, true));
        }
        return classSet;
    }
}
