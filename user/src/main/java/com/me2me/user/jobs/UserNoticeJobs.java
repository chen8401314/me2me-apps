package com.me2me.user.jobs;

import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/29.
 */
@Component
@EnableScheduling
public class UserNoticeJobs {

    @Autowired
    private UserService userService;

    // @Scheduled(cron = "0 * */1 * * ?")
    @Scheduled(cron = "* */15 * * * ?")
    public void push(){
        //System.out.println("fdsfds");
        userService.pushMessage();
    }

}
