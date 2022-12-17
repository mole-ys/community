package com.mole.community.controller;

import com.github.pagehelper.PageInfo;
import com.mole.community.entity.Comment;
import com.mole.community.entity.DiscussPost;
import com.mole.community.entity.User;
import com.mole.community.service.CommentService;
import com.mole.community.service.DiscussPostService;
import com.mole.community.service.UserService;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import com.mole.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Auther: ys
 * @Date: 2022/12/14 - 12 - 14 - 23:34
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            //异步，返回的是JSON格式
            return CommunityUtil.getJSONString(403,"你还没有登录哦！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}/{pageNum}",method = RequestMethod.GET)
    public String getDiscussPost(
            @PathVariable("discussPostId") int discussPostId, Model model, @PathVariable("pageNum") Integer pageNum){
        //查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        //查询帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //评论分页信息
        PageInfo<Comment> page = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostId, pageNum, 5);
        model.addAttribute("page",page);
        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = page.getList();
        //评论VO列表：VO表示显示的对象，组装成map
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                //一个评论的VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                // 评论的作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //查询回复列表(不分页)
                PageInfo<Comment> replyPage = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 1, Integer.MAX_VALUE);
                List<Comment> replyList = replyPage.getList();
                //回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 回复的作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }

                //把回复装进commentVo
                commentVo.put("replys", replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                //装入集合
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";
    }
}
