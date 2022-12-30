package com.mole.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/22 - 12 - 22 - 14:55
 */
public class Event {

    //事件主题（类型）
    private String topic;
    //事件发起者
    private int userId;
    //事件发生在哪个实体之上
    private int entityType;
    private int entityId;
    //实体作者
    private int entityUserId;
    //其他额外数据存到map中
    private Map<String, Object> data = new HashMap<>();


    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
