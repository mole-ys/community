package com.mole.community.controller;

import com.mole.community.entity.Comment;
import com.mole.community.service.CommentService;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @Auther: ys
 * @Date: 2022/12/16 - 12 - 16 - 19:54
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, int page){
        //可能会报错，后续会做异常处理
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        if(page == 0){page = 1;}
        return "redirect:/discuss/detail/" + discussPostId + "/" + page;
    }
}
