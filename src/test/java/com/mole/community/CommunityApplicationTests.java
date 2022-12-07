package com.mole.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
//在测试代码中加入配置类
@ContextConfiguration(classes = ContextConfiguration.class)
//实现这个接口来得到spring容器
class CommunityApplicationTests implements ApplicationContextAware {

	//记录自动传进来的ApplicationContext(spring容器)
	private ApplicationContext applicationContext;

	//实现接口需要重写这个方法，其参数就是spring容器，这个参数继承了BeanFactory
	//spring会检测到这个set方法，把自身传进来
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
	}
}
