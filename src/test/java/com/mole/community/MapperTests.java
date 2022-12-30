package com.mole.community;

import com.mole.community.dao.DiscussPostMapper;
import com.mole.community.dao.LoginTicketMapper;
import com.mole.community.dao.MessageMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.DiscussPost;
import com.mole.community.entity.LoginTicket;
import com.mole.community.entity.Message;
import com.mole.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 16:21
 */
@RunWith(SpringRunner.class)
//在测试代码中加入配置类
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
//实现这个接口来得到spring容器
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void TestSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149,0);
        discussPosts.forEach(System.out::println);
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("aaaaabb");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("aaaaabb");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("aaaaabb",1);
        loginTicket = loginTicketMapper.selectByTicket("aaaaabb");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversationsByUserId(111, 20);
        list.forEach(System.out::println);

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLettersByConversationId("111_112", 10);
        list.forEach(System.out::println);

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        //未读消息数量
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }

}
