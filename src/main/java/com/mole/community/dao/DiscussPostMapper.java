package com.mole.community.dao;

import com.mole.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 19:41
 */
@Repository
public interface DiscussPostMapper {
    //查询特定用户的帖子，若id为0，查询所有帖子(除了拉黑的)
    List<DiscussPost> selectDiscussPosts(int userId);

    //查询表里一共多少数据
    //@Param用于给参数起别名
    int selectDiscussPostRows(@Param("userId")int userId);
}
