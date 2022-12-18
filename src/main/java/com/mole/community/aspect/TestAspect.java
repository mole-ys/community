package com.mole.community.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: ys
 * @Date: 2022/12/17 - 12 - 17 - 21:28
 */
//@Component
//@Aspect
public class TestAspect {

    @Pointcut("execution(* com.mole.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
}
