package com.mole.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Auther: ys
 * @Date: 2022/12/30 - 12 - 30 - 22:18
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //声明核心配置类
        return builder.sources(CommunityApplication.class);
    }
}
