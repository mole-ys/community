package com.mole.community.service;

import com.mole.community.dao.LoginTicketMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.LoginTicket;
import com.mole.community.entity.User;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import com.mole.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:26
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    //注入一个固定值
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //根据userId查到user
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    //注册业务
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }

        //验证账号是否重复
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","账号已存在！");
            return map;
        }
        //验证邮箱是否重复
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //普通用户
        user.setType(0);
        //没有激活
        user.setStatus(0);
        //激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //设置随机头像 网址中%d替换随机整数
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        //thymeleaf上下文
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101（用户id）/code（激活码）
        //mybatis会自动回填id，配置文件中配置了
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        //用上下文渲染指定网页
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    //激活
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    //登录业务
    public Map<String,Object> login(String username, String password, long expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","账号不能为空！");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在！");
            return map;
        }
        //验证账号状态
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //到这里还没有问题，那么登录成功
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        //状态设置为有效
        loginTicket.setStatus(0);
        //设置过期时间,单位是毫秒
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        //返回结果需要放入凭证
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出业务
    public void logout(String ticket){
        //1表示凭证无效
        loginTicketMapper.updateStatus(ticket,1);
    }

    //忘记密码业务
    public Map<String,Object> forget(String email, String code, String newPassword, String realCode){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(code)){
            map.put("codeMsg","请输入验证码！");
            return map;
        }
//        if(StringUtils.isBlank(email)){
//            map.put("emailMsg","请输入邮箱！");
//            return map;
//        }
//        if(StringUtils.isBlank(newPassword)){
//            map.put("newPasswordMsg","请输入新的密码！");
//            return map;
//        }
        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg","该邮箱未被注册！");
            return map;
        }
        if(code.equals(realCode)){
            userMapper.updatePassword(user.getId(),CommunityUtil.md5(newPassword + user.getSalt()));
        }else {
            map.put("codeMsg","验证码不正确！");
            return map;
        }

        return map;
    }

    //发送验证码业务
    public Map<String,Object> getForgetCode(String email){
        Map<String,Object> map = new HashMap<>();
        if(!email.contains("@") || !email.contains(".")){
            map.put("emailMsg","邮箱格式错误！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg","该邮箱未被注册！");
            return map;
        }
        Context context = new Context();
        String code = CommunityUtil.generateUUID().substring(0, 6);
        context.setVariable("email",email);
        context.setVariable("code",code);
        String content = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(email,"找回你的账号",content);
        map.put("code",code);
        map.put("curTime",System.currentTimeMillis());
        return map;
    }

    //根据ticket获取LoginTicket对象
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //修改头像路径，返回更新行数
    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    //修改密码业务
    public Map<String, Object> updatePassword(User user,String oldPassword, String newPassword, String newPasswordTwice){
        Map<String, Object> map = new HashMap<>();
        String password = user.getPassword();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!password.equals(oldPassword)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg","密码不能为空！");
            return map;
        }
        if(!newPassword.equals(newPasswordTwice)){
            map.put("newPasswordTwiceMsg","两次输入的密码不一致！");
            return map;
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        if(newPassword.equals(password)){
            map.put("newPasswordMsg","新密码与原始密码相同！");
            return map;
        }
        userMapper.updatePassword(user.getId(),newPassword);
        return map;
    }
}
