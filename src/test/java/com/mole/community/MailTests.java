package com.mole.community;

import com.mole.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @Auther: ys
 * @Date: 2022/12/7 - 12 - 07 - 21:58
 */
@RunWith(SpringRunner.class)
//在测试代码中加入配置类
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    //mail核心组件
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextEmail(){
        mailClient.sendMail("18851900589@163.com","这是java自动发送的邮件","aaa");
    }
    @Test
    public void testHtmlEmail(){
        Context context = new Context();
        context.setVariable("username","hanqing");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("18851900589@163.com","给hq宝bui的html邮件",content);
    }
}
