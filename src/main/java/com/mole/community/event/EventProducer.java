package com.mole.community.event;

import com.alibaba.fastjson.JSONObject;
import com.mole.community.entity.Event;
import com.mole.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Auther: ys
 * @Date: 2022/12/22 - 12 - 22 - 15:02
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event) {
        //将事件发布到指定的主题，把event转换为json类型字符串
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }

}
