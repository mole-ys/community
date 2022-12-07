package com.mole.community.service;

import com.mole.community.dao.UserMapper;
import com.mole.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:26
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    //根据userId查到user
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

}
