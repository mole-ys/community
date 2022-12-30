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
    List<DiscussPost> selectDiscussPosts(int userId, int orderMode);

    //查询表里一共多少数据
    //@Param用于给参数起别名
    int selectDiscussPostRows(@Param("userId")int userId);

    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    //根据帖子id查询帖子
    DiscussPost selectDiscussPostById(int id);

    //更新commentCount评论数量
    int updateCommentCount(int id, int commentCount);

    //修改帖子类型
    int updateType(int id, int type);

    //修改帖子状态
    int updateStatus(int id, int status);

    //修改帖子分数
    int updateScore(int id, double score);
}
