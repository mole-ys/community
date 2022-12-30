package com.mole.community.controller;

import com.github.pagehelper.PageInfo;
import com.mole.community.entity.DiscussPost;
import com.mole.community.entity.User;
import com.mole.community.service.DiscussPostService;
import com.mole.community.service.LikeService;
import com.mole.community.service.UserService;
import com.mole.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:31
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model){
        List<DiscussPost> list = discussPostService.findAllDiscussPosts(0,0);
        //根据帖子和帖子归属的用户组成一个个map
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                //查询点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    //分页访问首页
    @RequestMapping(value = "/index/page/{pageNum}",method = RequestMethod.GET)
    public String getIndexPage(Model model, @PathVariable("pageNum") Integer pageNum,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        //pageNum是当前页码，limit是一页展示几条
        PageInfo<DiscussPost> page = discussPostService.findDiscussPostsPage(0, pageNum, 7, orderMode);
        model.addAttribute("page",page);
        List<DiscussPost> list = page.getList();
        //根据帖子和帖子归属的用户组成一个个map
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                //查询点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    //获取错误页面
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    //拒绝访问时的提示页面
    @RequestMapping(path = "/denied", method = {RequestMethod.GET})
    public String getDeniedPage() {
        return "/error/404";
    }
}
