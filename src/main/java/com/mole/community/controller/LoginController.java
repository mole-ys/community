package com.mole.community.controller;

import com.google.code.kaptcha.Producer;
import com.mole.community.entity.User;
import com.mole.community.service.UserService;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/8 - 12 - 08 - 15:19
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    //配置文件中配置的整个项目的路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //注册页面
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
    //登录页面
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //忘记密码页面
    @RequestMapping(value = "/forget",method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    //生成验证码随机图片
    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = producer.createText();
        //生成图片
        BufferedImage image = producer.createImage(text);

        //验证码文字存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            LOGGER.error("响应验证码失败" + e.getMessage());
        }
    }

    //注册
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    //只要传入的值与user的属性相匹配，MVC就会自动注入给对象
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            //跳转首页
            model.addAttribute("msg","注册成功，我们邮件向你的邮箱发送了一封激活邮件，请查收！");
            model.addAttribute("target","/index/page/1");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    //激活是否成功
    // http://localhost:8080/community/activation/101（用户id）/code（激活码）
    @RequestMapping(value = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId")Integer userId,@PathVariable("code")String code){
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS){
            //跳转首页
            model.addAttribute("msg","激活成功！！！您的账号可以正常使用了！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            //跳转首页
            model.addAttribute("msg","无效操作！该账号已经激活！");
            model.addAttribute("target","/index");
        }else {
            //跳转首页
            model.addAttribute("msg","激活码不正确，激活失败！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }


    //登录
    // 验证码放到了session里，所以我们需要session;
    // 如果登录成功，我们需要把ticket发放给客户端保存，需要使用cookie，所以需要HttpServletResponse
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response){
        String kaptcha = (String) session.getAttribute("kaptcha");
        //检查验证码  右边那个equals方法忽略大小写
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }
        //检查账号，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            //为true说明登录成功了
            //给客户端发一个cookie带上ticket
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            //凭证有效路径：整个项目内
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            //响应时添加cookie给浏览器
            response.addCookie(cookie);
            return "redirect:/index/page/1";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        //重定向默认是get请求
        return "redirect:/login";
    }


    //忘记密码提交
    @RequestMapping(value = "/forget/password",method = RequestMethod.POST)
    public String forget(String email, String code, String newPassword, HttpSession session, Model model){
        String realCode = (String)session.getAttribute("code");
        Map<String, Object> map = userService.forget(email, code, newPassword, realCode);
        System.out.println(map);
        if(map == null || map.isEmpty()){
            //跳转首页
            model.addAttribute("msg","密码修改成功，请重新登录！");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else {
            model.addAttribute("codeMsg",map.get("codeMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return "/site/forget";
        }
    }
    //获取验证码
    @ResponseBody
    @RequestMapping(value = "/forget/code",method = RequestMethod.GET)
    public String getForgetCode(String email, HttpSession session){
        if (StringUtils.isBlank(email)) {
            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
        }
        Map<String, Object> map = userService.getForgetCode(email);
        if (!StringUtils.isBlank((String)map.get("emailMsg"))) {
            return CommunityUtil.getJSONString(1, (String)map.get("emailMsg"));
        }
        session.setAttribute("code",map.get("code"));
        System.out.println(email);
        return CommunityUtil.getJSONString(0);
//        return "/site/forget";
    }

}
