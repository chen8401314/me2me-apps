package com.me2me.sms.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.common.sms.YunXinSms;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.channel.MessageChannel;
import com.me2me.sms.event.VerifyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
public class VerifyCodeListener {

    private final ApplicationEventBus applicationEventBus;

    private final MessageChannel messageChannel;

    @Autowired
    public VerifyCodeListener(ApplicationEventBus applicationEventBus,MessageChannel messageChannel){
        this.applicationEventBus = applicationEventBus;
        this.messageChannel = messageChannel;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void send(VerifyEvent verifyEvent){
        messageChannel.send(verifyEvent.getChannel(),verifyEvent.getVerifyCode(),verifyEvent.getMobile());
    }


}
