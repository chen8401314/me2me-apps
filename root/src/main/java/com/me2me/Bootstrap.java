package com.me2me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/26.
 */
@SpringBootApplication
@ImportResource(locations = {"classpath:spring/root-context.xml"})
@ComponentScan("com.me2me")
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class,args);
    }
}
