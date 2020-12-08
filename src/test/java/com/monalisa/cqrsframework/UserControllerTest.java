package com.monalisa.cqrsframework;

import com.monalisa.cqrsframework.api.dto.UserInfo;
import com.monalisa.cqrsframework.client.UserClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
@ContextConfiguration(locations = {"classpath:application.xml"})
public class UserControllerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private UserClient userController;

    @Test
    public void testUserRegister() {
        UserInfo userInfo = new UserInfo();
        userInfo.setIdcard("1234");
        userInfo.setName("Da Souche");
        UserInfo userInfoResponse = userController.registe(userInfo);
        Assert.assertNotNull(userInfoResponse);
        Assert.assertEquals(userInfoResponse.getIdcard(),"1234");
    }

    @Test
    public void testUserLogin() {
        UserInfo userInfo = new UserInfo();
        userInfo.setIdcard("1234");
        userInfo.setName("Da Souche");
        UserInfo userInfoResponse = userController.login(userInfo);
        Assert.assertNotNull(userInfoResponse);
        Assert.assertEquals(userInfoResponse.getIdcard(),"1234");
    }

    @Test
    public void testUserQuery() {
        UserInfo userInfo = new UserInfo();
        userInfo.setIdcard("1234");
        userInfo.setName("Da Souche");
        UserInfo userInfoResponse = userController.query(userInfo);
        Assert.assertNotNull(userInfoResponse);
        Assert.assertEquals(userInfoResponse.getIdcard(),"1234");
    }

}
