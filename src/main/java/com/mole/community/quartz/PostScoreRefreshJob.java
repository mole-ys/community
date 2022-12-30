package com.mole.community.quartz;

import com.mole.community.entity.DiscussPost;
import com.mole.community.service.DiscussPostService;
import com.mole.community.service.ElasticsearchService;
import com.mole.community.service.LikeService;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: ys
 * @Date: 2022/12/27 - 12 - 27 - 16:08
 */
@Component
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //牛客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！");
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //BoundSetOperations: Redis键的操作。适用于执行通用键-“绑定”操作到所有实现。
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if(operations.size() == 0){
            LOGGER.info("[任务取消] 没有需要刷新的帖子！");
            return;
        }
        LOGGER.info("[任务开始] 正在刷新帖子分数：" + operations.size());
        while (operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        LOGGER.info("[任务结束] 帖子分数刷新完毕！");
    }

    public void refresh(int postId){
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);

        if(discussPost == null){
            LOGGER.error("该帖子不存在：id = " + postId);
            return;
        }
        if(discussPost.getStatus() == 2){
            LOGGER.error("该帖子已被删除");
            return;
        }
        //算分
        //是否加精
        boolean wonderful = discussPost.getStatus() == 1;
        //评论数量
        int commentCount = discussPost.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + ((discussPost.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24));//1天
        //更新帖子的分数
        discussPostService.updateScore(postId, score);
        //同步搜索数据
        discussPost.setScore(score);
        elasticsearchService.saveDiscussPost(discussPost);
    }
}
