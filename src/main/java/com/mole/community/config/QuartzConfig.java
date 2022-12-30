package com.mole.community.config;

import com.mole.community.quartz.PostScoreRefreshJob;
import com.mole.community.quartz.TestJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Auther: ys
 * @Date: 2022/12/26 - 12 - 26 - 19:12
 */
// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail，任务详情
    //@Bean
    public JobDetailFactoryBean testJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(TestJob.class);
        factoryBean.setName("testJob");
        factoryBean.setGroup("testJobGroup");
        //声明任务是否长久保存
        factoryBean.setDurability(true);
        //任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)，触发器
    //CronTriggerFactoryBean：复杂的，如间隔每周，每月需要执行的
    //@Bean
    public SimpleTriggerFactoryBean testTrigger(JobDetail testJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(testJobDetail);
        factoryBean.setName("testTrigger");
        factoryBean.setGroup("testTriggerGroup");
        //多长时间执行这个任务
        factoryBean.setRepeatInterval(3000);
        //存储job的状态，用哪个对象来存
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 配置JobDetail，任务详情
    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("PostScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        //声明任务是否长久保存
        factoryBean.setDurability(true);
        //任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        //多长时间执行这个任务
        //这里配置5分钟
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        //存储job的状态，用哪个对象来存
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
