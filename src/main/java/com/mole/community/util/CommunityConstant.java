package com.mole.community.util;

/**
 * @Auther: ys
 * @Date: 2022/12/8 - 12 - 08 - 17:36
 * 常量接口，主要是激活状态
 *
 * 接口里面所有的属性都是public static final
 */
public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 0;

    //重复激活
    int ACTIVATION_REPEAT = 1;

    //激活失败
    int ACTIVATION_FAILURE = 2;

    //默认状态登录凭证的超时时间 12小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //记住状态登录凭证的超时时间 100天
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}