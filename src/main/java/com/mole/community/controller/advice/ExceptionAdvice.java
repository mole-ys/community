package com.mole.community.controller.advice;

import com.mole.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: ys
 * @Date: 2022/12/17 - 12 - 17 - 18:35
 */
//注解只去扫描带有controller注解的bean
@ControllerAdvice(annotations = {Controller.class})
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    //处理所有异常
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器发生异常：" + e.getMessage());
        //每一个element记录一条异常的信息
        for (StackTraceElement element : e.getStackTrace()){
            LOGGER.error(element.toString());
        }
        //判断是普通请求还是异步请求，固定写法
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)){
            //返回普通字符串，在前端手动转JSON
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
