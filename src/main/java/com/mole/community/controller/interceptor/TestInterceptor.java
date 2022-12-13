package com.mole.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: ys
 * @Date: 2022/12/11 - 12 - 11 - 21:30
 */
@Component
//这个接口里面所有的方法前面都有个default，表示默认是实现的（空实现），所以不强制实现所有方法
public class TestInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestInterceptor.class);

    //在controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.debug("preHandle:" + handler.toString());
        //false表示取消请求，下面的controller就不会执行了
        return true;
    }

    //在controller之后模板引擎TemplateEngine执行前执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.debug("postHandle:" + handler.toString());
    }

    //在模板引擎TemplateEngine执行完之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOGGER.debug("afterCompletion:" + handler.toString());

    }
}
