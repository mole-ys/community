package com.mole.community.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mole.community.dao.MessageMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/16 - 12 - 16 - 22:05
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 获取会话列表，返回的是最新的一条私信
     * @param userId
     * @param limit
     * @param pageNum
     * @return 这一页会话列表的最新的一条私信的分页数据
     */
    public PageInfo<Message> findConversations(int userId, int limit, int pageNum){
        PageHelper.startPage(pageNum,limit);
        List<Message> messages = messageMapper.selectConversationsByUserId(userId, 0);
        PageInfo<Message> pageInfo = new PageInfo<>(messages,5);
        return pageInfo;
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    /**
     *  获取会话的所有私信
     * @param conversationId
     * @param limit
     * @param pageNum
     * @return 这一页私信的分页数据
     */
    public PageInfo<Message> findLetters(String conversationId, int limit, int pageNum){
        PageHelper.startPage(pageNum,limit);
        List<Message> messages = messageMapper.selectLettersByConversationId(conversationId, 0);
        PageInfo<Message> pageInfo = new PageInfo<>(messages, 5);
        return pageInfo;
    }
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }
}
