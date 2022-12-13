package com.mole.community.util;

import com.mole.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Auther: ys
 * @Date: 2022/12/11 - 12 - 11 - 22:22
 * 持有用户信息，由于代替session对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //请求结束清理user
    public void clear(){
        users.remove();
    }
}
