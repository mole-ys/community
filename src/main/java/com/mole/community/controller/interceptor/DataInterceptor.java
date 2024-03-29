package com.mole.community.controller.interceptor;

import com.mole.community.entity.User;
import com.mole.community.service.DataService;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: ys
 * @Date: 2022/12/26 - 12 - 26 - 16:52
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        //统计DAU
        User user = hostHolder.getUser();
        if (user != null){
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
