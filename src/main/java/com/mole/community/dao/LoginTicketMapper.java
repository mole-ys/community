package com.mole.community.dao;

import com.mole.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @Auther: ys
 * @Date: 2022/12/9 - 12 - 09 - 16:05
 */
@Repository
@Deprecated
public interface LoginTicketMapper {
    //使用注解方式写sql,

    //插入一条数据
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    //声明主键自动生成,生成后自动注入哪个属性
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //用ticket查询用户
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证状态
    @Update({
            "update login_ticket set status=#{status} where ticket = #{ticket}"
    })
    int updateStatus(String ticket, int status);
}
