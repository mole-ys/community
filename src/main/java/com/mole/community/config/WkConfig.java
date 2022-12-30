package com.mole.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @Auther: ys
 * @Date: 2022/12/27 - 12 - 27 - 20:42
 */
//因为有config注解，spring会以为这是一个配置类，会去初始化他，其实他并不是配置类
@Configuration
public class WkConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    //初始化方法
    public void init() {
        // 创建wk图片目录
        File file = new File(wkImageStorage);
        if(!file.exists()){
            file.mkdir();
            LOGGER.info("创建WK图片目录：" + wkImageStorage);
        }
    }
}
