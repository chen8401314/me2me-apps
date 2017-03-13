package com.me2me.sms.listener;

import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.CommonUtils;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.channel.MessageChannel;
import com.me2me.sms.event.VerifyEvent;
import com.me2me.sms.exception.SendMessageLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
@Slf4j
public class VerifyCodeListener {

    private final ApplicationEventBus applicationEventBus;

    private final MessageChannel messageChannel;

    private final CacheService cacheService;

    private Splitter splitter = Splitter.on("@").trimResults();

    /**
     * redis 验证码缓存key前缀
     */
    private static final String VERIFY_PREFIX = "verify:";

    /**
     * 验证手机号码
     */
    private static final String VERIFY_MOBILE_PREFIX = "verify:mobile:count:";

    /**
     * 每日发送验证码上线次数为20次
     */
    private static final int SEND_MESSAGE_LIMIT = 20;

    @Autowired
    public VerifyCodeListener(ApplicationEventBus applicationEventBus,
                              MessageChannel messageChannel,
                              CacheService cacheService){
        this.applicationEventBus = applicationEventBus;
        this.messageChannel = messageChannel;
        this.cacheService = cacheService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void send(VerifyEvent verifyEvent) {
    	if(verifyEvent.getIsTest() == 1){
    		String verifyCode = "1234";
    		this.cacheService.setex(VERIFY_PREFIX + verifyEvent.getMobile(), verifyCode + "@" + System.currentTimeMillis(), 5 * 60);
    		return;
    	}
    	
        // 尝试从缓存中获取验证码信息
        String verifyCodeAndSendTimeMillis = cacheService.get(VERIFY_PREFIX + verifyEvent.getMobile());
        if (StringUtils.isEmpty(verifyCodeAndSendTimeMillis)) {
            // 为空的情况
            String verifyCode = CommonUtils.getRandom("", 4);
            verifyEvent.setVerifyCode(verifyCode);
            if (checkSendLimit(verifyEvent.getMobile())) {
                this.cacheService.setex(VERIFY_PREFIX + verifyEvent.getMobile(), verifyCode + "@" + System.currentTimeMillis(), 5 * 60);
                messageChannel.send(verifyEvent.getChannel(),verifyEvent.getVerifyCode(),verifyEvent.getMobile());
            } else {
                log.error("手机验证码次数超过上限，每日只能发送" + SEND_MESSAGE_LIMIT + "次短信");
                throw new SendMessageLimitException("手机验证码次数超过上限，每日只能发送" + SEND_MESSAGE_LIMIT + "次短信");
            }
        } else {
            // 不为空的情况
            if (checkSendLimit(verifyEvent.getMobile())) {
                String verifyCode = splitter.splitToList(verifyCodeAndSendTimeMillis).get(0);
                long sendTime = Long.parseLong(splitter.splitToList(verifyCodeAndSendTimeMillis).get(1));
                if (System.currentTimeMillis() - sendTime > TimeUnit.MINUTES.toMillis(1)) {
                    if (checkSendLimit(verifyEvent.getMobile())) {
                        verifyEvent.setVerifyCode(verifyCode);
                        this.cacheService.setex(VERIFY_PREFIX + verifyEvent.getMobile(), verifyCode + "@" + System.currentTimeMillis(), 5 * 60);
                        messageChannel.send(verifyEvent.getChannel(),verifyEvent.getVerifyCode(),verifyEvent.getMobile());
                    }
                }else {
                    log.error("每分钟只能发送一次验证码");
                    throw new SendMessageLimitException("每分钟只能发送一次验证码");
                }
            }else{
                log.error("手机验证码次数超过上限，每日只能发送" + SEND_MESSAGE_LIMIT + "次短信");
                throw new SendMessageLimitException("手机验证码次数超过上限，每日只能发送" + SEND_MESSAGE_LIMIT + "次短信");
            }
        }
    }
    private boolean checkSendLimit(String mobileNo) {
        String result = cacheService.get(VERIFY_MOBILE_PREFIX+mobileNo);
        if(StringUtils.isEmpty(result)){
            // 未找到发送手机号码的发送次数
            cacheService.setex(VERIFY_MOBILE_PREFIX+mobileNo,"1",60*60*24);
            return Boolean.TRUE;
        }else{
            // 已经找到发送次数
            Integer count = Integer.parseInt(result);
            count++;
            if(count>SEND_MESSAGE_LIMIT){
                return Boolean.FALSE;
            }
            //
            cacheService.setex(VERIFY_MOBILE_PREFIX+mobileNo,count.toString(),60*60*24);
            return Boolean.TRUE;
        }


    }


}
