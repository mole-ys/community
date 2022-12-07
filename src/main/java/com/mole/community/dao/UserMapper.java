package com.mole.community.dao;

import com.mole.community.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 15:57
 */
@Repository
public interface UserMapper {

    //根据id查询用户
    User selectById(int id);

    //根据用户名查询用户
    User selectByName(String username);

    //根据邮箱查用户
    User selectByEmail(String email);

    //增加一个用户，返回的是增加数据的行数
    int insertUser(User user);

    //修改用户状态
    int updateStatus(int id,int status);

    //更新用户头像
    int updateHeader(int id,String headerUrl);

    //更新密码
    int updatePassword(int id,String password);
}
