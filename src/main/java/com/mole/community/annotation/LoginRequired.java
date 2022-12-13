package com.mole.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: ys
 * @Date: 2022/12/13 - 12 - 13 - 17:41
 */
//这个注解表示登录用户才能访问
//使用元注解对其描述
//表示这个注解可以用在方法之上
@Target(ElementType.METHOD)
//表示在程序运行时注解有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
