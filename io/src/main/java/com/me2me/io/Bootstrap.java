package com.me2me.io;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
public class Bootstrap {
    public static void main(String[] args) throws InterruptedException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/io-dubbo-provider.xml");
        ctx.start();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                ctx.close();
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
