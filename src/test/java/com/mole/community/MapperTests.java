package com.mole.community;

import com.mole.community.dao.DiscussPostMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.DiscussPost;
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
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149);
        discussPosts.forEach(System.out::println);
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }
}