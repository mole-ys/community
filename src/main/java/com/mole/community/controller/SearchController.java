package com.mole.community.controller;

import com.mole.community.entity.DiscussPost;
import com.mole.community.entity.Page;
import com.mole.community.service.ElasticsearchService;
import com.mole.community.service.LikeService;
import com.mole.community.service.UserService;
import com.mole.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/23 - 12 - 23 - 19:43
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    //搜到帖子以后还需要展示帖子作者，点赞数量
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        Object[] result;
        List<DiscussPost> searchResult = null;
        try {
            result = elasticsearchService.searchDiscussPost(keyword, page.getOffset(), page.getLimit());
        } catch (IOException e) {
            throw new RuntimeException("搜索出现异常！" + e);
        }
        searchResult = (List<DiscussPost>) result[1];
        //聚合
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            for (DiscussPost post : searchResult){
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post", post);
                //作者
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (Integer)result[0]);

        return "/site/search";
    }
}
