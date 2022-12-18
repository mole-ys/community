package com.mole.community.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mole.community.dao.MessageMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.Message;
import com.mole.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/16 - 12 - 16 - 22:05
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    //增加一条私信
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    //把消息变成已读
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }

    //删除私信
    public int deleteMessage(List<Integer> id){
        return messageMapper.updateStatus(id,2);
    }
}
