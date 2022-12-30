package com.mole.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Auther: ys
 * @Date: 2022/12/18 - 12 - 18 - 16:40
 */
@Configuration
public class RedisConfig {

    @Bean
    //需要注入连接工厂
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //指定数据转换方式
        //设置key的序列化方式  这个方法返回一个能够序列化字符串的序列化器
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式  序列化成JSON
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //设置生效
        template.afterPropertiesSet();
        return template;
    }
}
