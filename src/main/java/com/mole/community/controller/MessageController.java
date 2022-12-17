package com.mole.community.controller;

import com.github.pagehelper.PageInfo;
import com.mole.community.entity.Message;
import com.mole.community.entity.User;
import com.mole.community.service.MessageService;
import com.mole.community.service.UserService;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/16 - 12 - 16 - 22:17
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    //私信列表
    @RequestMapping(value = "/letter/list/{pageNum}",method = RequestMethod.GET)
    public String getLetterList(Model model, @PathVariable("pageNum") Integer pageNum){
        User user = hostHolder.getUser();
        //查询会话列表
        PageInfo<Message> page = messageService.findConversations(user.getId(), 5, pageNum);
        model.addAttribute("page", page);
        //查到的是每个会话最新的一条message组成的list
        List<Message> conversationList = page.getList();

        //还需要获得会话的未读数量，每一个会话的未读数量，每一个会话有多少私信。
        //查询每一个会话的相关信息
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message message : conversationList){
                Map<String, Object> map = new HashMap<>();
                //会话
                map.put("conversation", message);
                //每一个会话的私信数量
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                //每一个会话的未读消息数量
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //显示私信对方的头像
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //查询所有未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(value = "/letter/detail/{conversationId}/{pageNum}", method = RequestMethod.GET)
    public String getLetterDetail(
            @PathVariable("conversationId") String conversationId,  @PathVariable("pageNum") Integer pageNum, Model model){
        PageInfo<Message> page = messageService.findLetters(conversationId, 5, pageNum);
        model.addAttribute("page", page);
        List<Message> letterList = page.getList();

        //存放fromUser的数据
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message message : letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";
    }

    //获取私信来自哪个用户
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id0);
        }else {
            return userService.findUserById(id1);
        }
    }
}
