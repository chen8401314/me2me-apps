package com.me2me.sms.service;
import com.google.common.base.Splitter;
import com.me2me.cache.service.CacheService;
import com.me2me.common.sms.YunXinSms;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.channel.MessageChannel;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.event.VerifyEvent;
import com.me2me.sms.listener.VerifyCodeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Autowired
    private CacheService cacheService;

    private static final String VERIFY_PREFIX = "verify:";

    private Splitter splitter = Splitter.on("@").trimResults();

    @Autowired
    private VerifyCodeListener verifyCodeListener;

    /**
     * 发送验证码
     * @param verifyDto
     */
    @Override
    public void send(VerifyDto verifyDto){
        // applicationEventBus.post(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel()));
        verifyCodeListener.send(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel()));
    }

    /**
     * 校验验证码
     * @param verifyDto
     */
    @Override
    public boolean verify(VerifyDto verifyDto) {
        // 网易云信通道验证
        if(verifyDto.getChannel()== ChannelType.NET_CLOUD_SMS.index) {
            return YunXinSms.verify(verifyDto.getMobile(), verifyDto.getVerifyCode());
        }
        // 获取redis中的数据
        String verifyCodeAndSendTimeMillis = cacheService.get(VERIFY_PREFIX+verifyDto.getMobile());
        if(!StringUtils.isEmpty(verifyCodeAndSendTimeMillis)){
            String verifyCode = splitter.splitToList(verifyCodeAndSendTimeMillis).get(0);
            if(verifyDto.getVerifyCode().equals(verifyCode))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String nickName ,String awardName ,String mobile ,String OperateMobile) {
        Boolean isTrue = YunXinSms.sendSms2(nickName ,awardName ,mobile ,OperateMobile);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

}
