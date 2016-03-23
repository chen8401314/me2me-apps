package com.me2me.user;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/16.
 */
@Component
public class ApplicaitonContext implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("***************************************************************");
        System.out.println("***************************************************************");
        System.out.println("***************************************************************");
        System.out.println("***************************************************************");
        System.out.println("***************************************************************");
        System.out.println(applicationContext.getEnvironment().getDefaultProfiles()[0]);

    }
}
