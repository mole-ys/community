package com.mole.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Auther: ys
 * @Date: 2022/12/28 - 12 - 28 - 18:07
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
