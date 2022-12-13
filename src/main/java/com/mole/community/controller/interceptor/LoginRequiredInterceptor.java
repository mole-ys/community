package com.mole.community.controller.interceptor;

import com.mole.community.annotation.LoginRequired;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Auther: ys
 * @Date: 2022/12/13 - 12 - 13 - 17:53
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    //尝试获取当前用户，取到了就是登陆了
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //SpringMVC的功能，拦截到的是方法的话，对象就是HandlerMethod类型
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取拦截到的method对象
            Method method = handlerMethod.getMethod();
            //尝试去取这个注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //当前方法需要登录，但user是null
            if(loginRequired != null && hostHolder.getUser() == null){
                //使用response重定向
                //从请求中可以直接去上下文路径
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
