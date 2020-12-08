# 基于CQRS的业务开发规范

## 原则
基于DDD的代码实践规范，参考其它项目DDD最佳实践。
下文以Command为例说明，Query只是注解使QueryService不同，其它并无差别。

## 准备

<dependency>
   <groupId>com.souche</groupId>
       <artifactId>cqrs-framework</artifactId>
       <version>1.0.0</version>
</dependency>

## 定义命令对象
*命令对象继承自command
```
@Data
public class AddCustomerCmd extends Command {

    @Override
    public String toString() {
        return "AddCustomerCmd{}";
    }
}
```

## 定义命令处理器
命令处理器的定义支持两种方式：类处理器和方法处理器，分别如下：
### 类处理器
@CommandHandler声明在相应的类上，此种编码模式的命令处理器必须包括一个名称为execute的方法，且方法有且只能有一个表示要处理的命令对像的参数。
```
@CommandHandler
public class AddCustomerCmdExt{
    private Logger logger = LoggerFactory.getLogger(AddCustomerCmdExt.class);
    
    public UserInfo execute(AddCustomerCmd cmd) {
        logger.info("Start processing command:" + cmd);

        logger.info("End processing command:" + cmd);
        return new UserInfo();
    }
}
```

### 方法处理器
@CommandHandler注解在方法上，方法名称可以随意，只要符合java规范就好（编译器认就行，最好符合最佳统程规范），方法有且只能有一个表示要处理的命令对像的参数。
```
@Component
public class AddCustomerCmdMethodExt {
    private Logger logger = LoggerFactory.getLogger(AddCustomerCmdMethodExt.class);


    @CommandHandler
    public HandleResult handle(AddCustomerMethodCmd cmd) {
        logger.info("Start processing command:" + cmd);

        logger.info("End processing command:" + cmd);
        HandleResult<String> result = new HandleResult<String>();
        return result;
    }
}
```

### 命令拦截器
#### 前置/后置拦截器
拦截器分为非限定命令拦截器和限定命令拦截器，所有拦截器都要实现ICommandInterceptor接口。
@PreCommandInterceptor注解来标注实现了ICommandInterceptor接口的是类是前置拦截器，命令发出时，此类的preIntercept方法将在command命令执行器执行之前被调用。
@PostCommandInterceptor注解来标注实现了ICommandInterceptor接口的是类是后置拦截器，命令发出时，此类的postIntercept方法将在command命令执行器执行之后被调用。
示例代码如下：
```
@PreCommandInterceptor
@PostCommandInterceptor
public class GlobalCommandInterceptor implements ICommandInterceptor<Command> {

    @Override
    public void preIntercept(Command command) {
        System.out.println("pre GlobalCommandInterceptor " + command);
    }

    @Override
    public void postIntercept(Command command, Response response) {
        System.out.println("post GlobalCommandInterceptor " + command);
    }
}
```
#### 非限定命令拦截器
使用@PreCommandInterceptor和@PostCommandInterceptor注解时，不指定其commands属性时，表示该拦截器将拦截所有其实现接口ICommandInterceptor中所指定的泛化类型所表示的命令对象及其子对象，如上例中

class GlobalCommandInterceptor implements ICommandInterceptor<Command>
表示GlobalCommandInterceptor将拦截所有Command及其子类型的命令对象。
注意：此模式使用时，参数化类型必须指定，否则报错。
#### 限定命令拦截器
使用@PreCommandInterceptor和@PostCommandInterceptor注解时，指定其commands属性时，表示该拦截器将拦截所有在commands属性中指定的命令及其子类型对象。如下：
```
@PreCommandInterceptor(commands = {RemoveCustomerCmd.class})
@PostCommandInterceptor(commands = {AddCustomerCmd.class,RemoveCustomerCmd.class})
public class CustomerCommandInterceptor implements ICommandInterceptor {

    @Override
    public void preIntercept(Command command) {
        System.out.println("pre CustomerCommandInterceptor " + command);
    }

    @Override
    public void postIntercept(Command command, Response response) {
        System.out.println("post CustomerCommandInterceptor " + command);
    }
}
```

## 在spring容器中配置
Bootstrap配置必须有，并指明要扫描的packages，以加载事件处理器。
packages属性配置为要扫描的前面出现的注解标注的类所在的位置。
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-3.0.xsd">
    <import resource="classpath*:application-optimus-datasource.xml"/>
    <import resource="classpath*:system-context-jpa.xml"></import>

    <context:component-scan base-package="com.monalisa.cqrsframework"/>
    
    <bean id="bootstrap" class="com.monalisa.cqrsframework.boot.Bootstrap" init-method="init">
        <property name="packages">
            <list>
                <value>com.souche.cqrs.application</value>
            </list>
        </property>
    </bean>
    <!--配置CommandBus-->
    <bean id="commandBus" class="com.monalisa.cqrsframework.command.CommandBus"/>
    <!--配置QueryBus-->
    <bean id="queryBus" class="com.monalisa.cqrsframework.query.QueryBus"/>
    <!--配置EventBus-->
    <bean id="eventBus" class="com.monalisa.cqrsframework.event.EventBus"/>
</beans>
```



## 测试
事件发送都以CommandBus发送。
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class DomainEventTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ICommandBus commandBus;
    
    @Test
    public void testCustomerAddedEvent() {
        AddCustomerCmd addCustomerCmd = new AddCustomerCmd();
        commandBus.send(addCustomerCmd);
    }
}
```
## 问题
所有bus的接口全限定名称一样了，在微服务架构下，dubbo client和dubbo server配置问题
解决方案
CommandBus/QueryBus等暴露成Spring bean时，对于A服务依赖B/C两个服务时，这些Bus在A端要与置两次，B/C里只配置一次，但B/C中都要指定Bus的dubbo group，A上的dubbo client配置根据group决定要调到哪去。
B/C两个应用分别封装一个独立的CommandGateway、QueryGateway接口，全限定名根据服务域自定义（不会重名），作为dubbo服务暴露，Commandbus和QueryBus作为上述接口实现的实例属性即可，同时，gateway接口还可以统一处理command异常，封装成Result返回dubbo client。gateway接口还可以统一控制事务（推荐）
dubbo接口超时重试问题
解决方案
所有CommandGateway接口的超时重试机制应该关闭，因为command很难做到幂等，重试会造成系统状态不一致。
所有QueryGateway配统一重试机制。