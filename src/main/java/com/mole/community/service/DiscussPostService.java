package com.mole.community.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mole.community.dao.DiscussPostMapper;
import com.mole.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:19
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;


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

}
