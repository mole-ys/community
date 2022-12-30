package com.mole.community.config;

import com.mole.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: ys
 * @Date: 2022/12/11 - 12 - 11 - 21:52
 */

@Configuration
//配置拦截器，需要实现这个接口
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private TestInterceptor testInterceptor;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    //@Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;
    @Autowired
    private DataInterceptor dataInterceptor;

    //注册接口就是实现这个方法
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //.excludePathPatterns()排除一些不用拦截的路径
        //.addPathPatterns()添加一些需要拦截的路径
        registry.addInterceptor(testInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        //registry.addInterceptor(loginRequiredInterceptor)
        //        .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
