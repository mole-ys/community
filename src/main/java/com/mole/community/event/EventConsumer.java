package com.mole.community.event;

import com.alibaba.fastjson.JSONObject;
import com.mole.community.entity.DiscussPost;
import com.mole.community.entity.Event;
import com.mole.community.entity.Message;
import com.mole.community.service.DiscussPostService;
import com.mole.community.service.ElasticsearchService;
import com.mole.community.service.MessageService;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @Auther: ys
 * @Date: 2022/12/22 - 12 - 22 - 15:05
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    //一个可以执行定时任务的线程池
    private ThreadPoolTaskScheduler taskScheduler;

    //消费事件的方法
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null){
            LOGGER.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            LOGGER.error("消息格式错误！");
            return;
        }

        // 发送站内通知，构造message对象
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        //事件发起者
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }


    //消费发帖事件
    //把事件中的帖子信息存到es服务器
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            LOGGER.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            LOGGER.error("消息格式错误！");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
    //消费删帖事件
    //把事件中的帖子信息存到es服务器
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            LOGGER.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            LOGGER.error("消息格式错误！");
            return;
        }

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            LOGGER.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            LOGGER.error("消息格式错误！");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(cmd);
            LOGGER.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            LOGGER.error("生成长图失败：" + e.getMessage());
        }

        // 启用定时器，监视该图片，一旦生成了，则上传至七牛云
        UploadTask task = new UploadTask(fileName, suffix);
        //传进任务，每隔500s执行一次
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    class UploadTask implements Runnable{

        //文件名称
        private String fileName;
        //文件后缀
        private String suffix;
        //启动任务的返回值
        private Future future;
        //开始时间
        private long startTime;
        //上传次数
        private int uploadTimes;

        //这两个参数必须传
        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            //终止条件
            //生成图片失败
            if(System.currentTimeMillis() - startTime > 30000) {
                LOGGER.error("执行时间过长，终止任务：" + fileName);
                future.cancel(true);
                return;
            }
            //上传失败
            if(uploadTimes >= 3) {
                LOGGER.error("上传次数过多，终止任务：" + fileName);
            }
            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if(file.exists()){
                //当生成图片大时可能会出现wk还未写入的情况，这里我sleep了一下...
                try {
                    Thread.sleep(500);
                    LOGGER.info("wk正在生成图片...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                LOGGER.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                //生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                //指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Region.huadong()));
                try {
                    //开始上传图片
                    //见官方手册
                    Response response = manager.put(
                            path, fileName, uploadToken, null,
                            "image/" + suffix.substring(suffix.lastIndexOf(".") + 1), false
                    );
                    //处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if(json == null || json.get("code") == null || !json.get("code").toString().equals("0")){
                        LOGGER.info(String.format("第%d次上传失败[%s]", uploadTimes, fileName));
                    }else {

                        LOGGER.info(String.format("第%d次上传成功[%s]", uploadTimes, fileName));
                        future.cancel(true);
                    }
                }catch (QiniuException e){
                    LOGGER.info(String.format("第%d次上传失败[%s]", uploadTimes, fileName));
                }
            }else {
                LOGGER.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
