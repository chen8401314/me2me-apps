package com.me2me.sms.service;
import com.me2me.common.sms.YunXinSms;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.event.VerifyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private ApplicationEventBus applicationEventBus;

    /**
     * 发送验证码
     * @param verifyDto
     */
    @Override
    public void send(VerifyDto verifyDto){
        applicationEventBus.post(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel()));
    }

    /**
     * 校验验证码
     * @param verifyDto
     */
    @Override
    public boolean verify(VerifyDto verifyDto) {
        return YunXinSms.verify(verifyDto.getMobile(),verifyDto.getVerifyCode());
    }


}
