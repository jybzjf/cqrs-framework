package com.monalisa.cqrsframework.command;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.monalisa.cqrsframework.boot.ProxyTargetUtils;
import com.monalisa.cqrsframework.dto.Command;
import com.monalisa.cqrsframework.exception.BasicErrorCode;
import com.monalisa.cqrsframework.dto.Response;
import com.monalisa.cqrsframework.exception.BizException;
import com.monalisa.cqrsframework.exception.ComponentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command Hub holds all the important information about Command
 */
@SuppressWarnings("rawtypes")
@Component
@Slf4j
public class CommandHub {

    /**
     * key is CommandClz
     */
    private ListMultimap<Class, ICommandInterceptor> preInterceptors = LinkedListMultimap.create();
    /**
     * key is CommandClz
     */
    private ListMultimap<Class, ICommandInterceptor> postInterceptors = LinkedListMultimap.create();
    /**
     * 全局通用的PreInterceptors
     */
    private List<ICommandInterceptor> globalPreInterceptors = new ArrayList<>();
    /**
     * 全局通用的PostInterceptors
     */
    private List<ICommandInterceptor> globalPostInterceptors = new ArrayList<>();
    /**
     * key is CommandClz
     */
    private Map<Class, CommandInvocation> commandRepository = new HashMap<>();

    /**
     * This Repository is used for return right response type on exception scenarios
     * key is CommandClz
     * value is ResponseClz
     */
    private Map<Class, Class> responseRepository = new HashMap<>();

    public CommandInvocation getCommandInvocation(Class cmdClass) {
        CommandInvocation commandInvocation = commandRepository.get(cmdClass);
        if (commandRepository.get(cmdClass) == null) {
            throw new ComponentException(cmdClass + " is not registered in CommandHub, please register first");
        }
        return commandInvocation;
    }

    /**
     * 注册事件
     *
     * @param commandClz
     * @param executor
     */
    public void register(Class<? extends Command> commandClz, CommandInvocation executor) {
        commandRepository.put(commandClz, executor);
    }

    public void registerResponseRepository(Class commandClz, Class returnType) {
        responseRepository.put(commandClz, returnType);
    }


    public void registerPreInterceptors(Class<? extends Command> supportClass, ICommandInterceptor eventInterceptor) {
        preInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerPostInterceptors(Class<? extends Command> supportClass, ICommandInterceptor eventInterceptor) {
        postInterceptors.put(supportClass, eventInterceptor);
    }

    public void registerGlobalPreInterceptors(ICommandInterceptor eventInterceptor) {
        globalPreInterceptors.add(eventInterceptor);
    }

    public void registerGlobalPostInterceptors(ICommandInterceptor eventInterceptor) {
        globalPostInterceptors.add(eventInterceptor);
    }

    public <T> Response<T> getResponseInstance(Command cmd) {
        Class responseClz = this.responseRepository.get(cmd.getClass());
        try {
            return (Response<T>) responseClz.newInstance();
        } catch (Exception e) {
            return new Response<>();
        }
    }

    public <T> T doCommand(Command command){
        Response<T> response = Response.<T>buildSuccess();

        try {
            executePreInterceptors(command);
            CommandInvocation commandInvocation = getCommandInvocation(command.getClass());
            T result = commandInvocation.invoke(command);
            response.setData(result);

        } catch (Exception e) {
            log.error("Handle command <"+command.getClass().getName()+"> content is <"+command.getContext()+"> error!",e);
            response = Response.<T>buildFailure(BasicErrorCode.B_COMMON_ERROR.getErrCode(), BasicErrorCode.B_COMMON_ERROR.getErrDesc());
            throw new BizException("执行命令错误!",e);
            //ExceptionHandlerFactory.getExceptionHandler().handleException(command, response, e);
        }
        finally {
            executePostIntercept(command,response);
        }
        return response.getData();
    }
    public void executePreInterceptors(Command command) {
        //先执行全局的
        for (ICommandInterceptor globalPreInterceptor : globalPreInterceptors) {
            if (understandCommand(globalPreInterceptor, command.getClass())) {
                globalPreInterceptor.preIntercept(command);
            }
        }
        //再执行command特有的
        Class<?> superClass = command.getClass();
        while (Command.class.isAssignableFrom(superClass)) {
            List<ICommandInterceptor> preInterceptors = this.preInterceptors.get(superClass);
            for (ICommandInterceptor preInterceptor : preInterceptors) {
                preInterceptor.preIntercept(command);
            }
            superClass = superClass.getSuperclass();
        }
    }

    public <T> void executePostIntercept(Command command,Response<T> response) {
        try {
            //先执行command特有的
            Class<?> superClass = command.getClass();
            while (Command.class.isAssignableFrom(superClass)) {
                List<ICommandInterceptor> postInterceptors = this.postInterceptors.get(superClass);
                for (ICommandInterceptor postInterceptor : postInterceptors) {
                    postInterceptor.postIntercept(command, response);
                }
                superClass = superClass.getSuperclass();
            }
            //再执行全局的
            for (ICommandInterceptor globalPostInterceptor : globalPostInterceptors) {
                if (understandCommand(globalPostInterceptor, command.getClass())) {
                    globalPostInterceptor.postIntercept(command, response);
                }
            }

        } catch (Exception e) {
            log.error("postInterceptor error:" + e.getMessage(), e);
        }
    }


    private boolean understandCommand(ICommandInterceptor interceptor, Class commandExeClz) {
        ICommandInterceptor target = (ICommandInterceptor) ProxyTargetUtils.getTarget(interceptor);
        Type[] genType = target.getClass().getGenericInterfaces();
        for (Type type : genType) {
            ParameterizedTypeImpl paramType = (ParameterizedTypeImpl) type;
            if (paramType.getRawType().equals(ICommandInterceptor.class)) {
                return ((Class) (paramType.getActualTypeArguments()[0])).isAssignableFrom(commandExeClz);
            }
        }
        return false;
    }

}
