package com.mole.community.config;

import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: ys
 * @Date: 2022/12/24 - 12 - 24 - 18:52
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "unfollow"
                ).hasAnyAuthority(
                    AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/delete"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,AUTHORITY_USER
                )
                .antMatchers(
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//除了以上请求只有这三种用户有权限，其他请求都允许（未登录也有权限）
                .and().csrf().disable();
        //权限不够时的处理
        //用一个组件处理
        //authenticationEntryPoint：需要认证时的处理
        //accessDeniedHandler：权限不足时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        //如果这个值是XMLHttpRequest，那么表示是一个异步请求
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //表示内容是一个普通字符串
                            response.setContentType("application/plain");
                            response.setCharacterEncoding("utf-8");
                            //字符流输出这个内容
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦！"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                }).accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        //如果这个值是XMLHttpRequest，那么表示是一个异步请求
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //表示内容是一个普通字符串
                            response.setContentType("application/plain");
                            response.setCharacterEncoding("utf-8");
                            //字符流输出这个内容
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限！"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        // Security底层默认会拦截/logout请求，进行退出处理。
        // 覆盖它默认的逻辑，才能执行我们自己的代码
        // 我们的程序中并没有"/securitylogout"，这样的话他就不能拦截到logout请求了。
        http.logout().logoutUrl("/securitylogout");
    }
}
