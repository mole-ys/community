package com.mole.community.dao;

import com.mole.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/16 - 12 - 16 - 20:57
 */
@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversationsByUserId(int userId, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLettersByConversationId(String conversationId, int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);
}
