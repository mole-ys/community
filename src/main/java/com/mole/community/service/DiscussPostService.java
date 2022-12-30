package com.mole.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mole.community.dao.DiscussPostMapper;
import com.mole.community.dao.UserMapper;
import com.mole.community.entity.DiscussPost;
import com.mole.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: ys
 * @Date: 2022/12/6 - 12 - 06 - 20:19
 */
@Service
public class DiscussPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口：Cache, LoadingCache, AsyncLoadingCache

    //帖子列表的缓存
    private LoadingCache<String, PageInfo<DiscussPost>> postListCache;

    //帖子总数的缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    //初始化方法，初始化缓存
    @PostConstruct
    public void init() {
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, PageInfo<DiscussPost>>() {
                    @Override
                    public @Nullable PageInfo<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        String[] params = key.split(":");
                        if(params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        int pageNum = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //二级缓存
                        PageHelper.startPage(pageNum,limit);
                        LOGGER.debug("load post list from DB.");
                        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 1);
                        PageInfo<DiscussPost> pageInfo = new PageInfo<>(discussPosts,5);
                        return pageInfo;
                    }
                });//这个接口是查询数据库得到数据的办法。
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        LOGGER.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    public List<DiscussPost> findAllDiscussPosts(int userId, int orderMode){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(userId, orderMode);
        return discussPosts;
    }

    //offset起始行，limit每页显示最多数据数量
    public PageInfo<DiscussPost> findDiscussPostsPage(int userId,int pageNum,int limit, int orderMode){
        if(userId == 0 && orderMode == 1){
            return postListCache.get(pageNum + ":" + limit);
        }
        PageHelper.startPage(pageNum,limit);
        LOGGER.debug("load post list from DB.");
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(userId, orderMode);
        PageInfo<DiscussPost> pageInfo = new PageInfo<>(discussPosts,5);
        return pageInfo;
    }

    public int findDiscussPostsRows(int userId){
        if(userId == 0){
            return postRowsCache.get(userId);
        }
        LOGGER.debug("load post rows from DB.");
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

    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }
    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
