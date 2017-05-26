package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/5/26
 * Time :10:03
 */
public class StartNewEmotionInfoRequest extends Request{

    @Setter
    @Getter
    private int source;
    
    @Setter
    @Getter
    private String image;
    
    @Setter
    @Getter
    private int w;
    
    @Setter
    @Getter
    private int h;
}
