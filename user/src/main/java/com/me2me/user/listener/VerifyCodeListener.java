package com.me2me.user.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.common.sms.YunXinSms;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.user.event.VerifyEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
public class VerifyCodeListener {

    private final ApplicationEventBus applicationEventBus;


    @Autowired
    public VerifyCodeListener(ApplicationEventBus applicationEventBus){
        this.applicationEventBus = applicationEventBus;
    }

    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void verify(VerifyEvent verifyEvent){
        YunXinSms.verify(verifyEvent.getMobile(),verifyEvent.getVerifyCode());
    }

    @Subscribe
    public void send(VerifyEvent verifyEvent){
        YunXinSms.sendSms(verifyEvent.getMobile());
    }


}
