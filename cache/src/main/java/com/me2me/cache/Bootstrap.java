package com.me2me.cache;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.me2me.cache.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.concurrent.CountDownLatch;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
public class Bootstrap {
    public static void main(String[] args) throws InterruptedException {
//        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/cache-dubbo-provider.xml");
//        ctx.start();
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                ctx.close();
//            }
//        });
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        countDownLatch.await();

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/cache-context.xml");
        CacheService cacheService = ctx.getBean(CacheService.class);
        cacheService.set("fdsfd","fdsfds");


    }
}
