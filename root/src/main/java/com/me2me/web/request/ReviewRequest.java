package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :16:55
 */
public class ReviewRequest extends Request{

    @Getter
    @Setter
    private long cid;

    @Getter
    @Setter
    private String review;

    @Setter
    @Getter
    private int type;
    
    @Setter
    @Getter
    private int isAt;

    @Setter
    @Getter
    private long atUid;

    @Setter
    @Getter
    private String extra;
}
