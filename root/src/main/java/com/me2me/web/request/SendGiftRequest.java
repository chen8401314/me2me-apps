package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/9/6
 * Time :10:03
 */
public class SendGiftRequest extends Request{

    @Setter
    @Getter
    private long topicId;
    
    @Setter
    @Getter
    private long giftId;
    
    @Setter
    @Getter
    private int giftCount;
    
    @Setter
    @Getter
    private String onlyCode;
    
    @Setter
    @Getter
    private int source;
}
