package com.me2me.user.jobs;

import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/29.
 */
@Component
//@EnableScheduling
@Slf4j
public class UserNoticeJobs {

    @Autowired
    private UserService userService;

//    @Scheduled(fixedDelay = 1000*60*5)
//    @Async("false")
    public void push(){
        log.info("execute schedule by user notice jobs ... ");
        userService.pushMessage();
        log.info("push message by jobs end ... ");
    }

}
