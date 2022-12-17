package com.mole.community.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mole.community.dao.DiscussPostMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.DiscussPost;
import com.mole.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:19
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> findAllDiscussPosts(int userId){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(userId);
        return discussPosts;
    }
    //offset起始行，limit每页显示最多数据数量
    public PageInfo<DiscussPost> findDiscussPostsPage(int userId,int pageNum,int limit){
        PageHelper.startPage(pageNum,limit);
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(userId);
        PageInfo<DiscussPost> pageInfo = new PageInfo<>(discussPosts,5);
        return pageInfo;
    }

    public int findDiscussPostsRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //处理标签字符
        //SpringMVC自带工具，把标签转换为转译字符
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新评论数量
    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
