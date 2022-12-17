package com.mole.community.dao;

import com.mole.community.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/15 - 12 - 15 - 17:14
 */
@Repository
public interface CommentMapper {

    //根据实体类查询评论
    List<Comment> selectCommentByEntity(int entityType, int entityId, int limit);

    //查询数据条数
    int selectCountByEntity(int entityType, int entityId);

    //添加评论
    int insertComment(Comment comment);
}
