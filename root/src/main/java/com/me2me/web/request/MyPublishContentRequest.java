package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/28
 * Time :16:41
 */
public class MyPublishContentRequest extends Request {


    @Getter
    @Setter
    private int sinceId;

    @Setter
    @Getter
    private long customerId;

    @Setter
    @Getter
    private int type;

    @Setter
    @Getter
    private long updateTime;

    @Setter
    @Getter
    private int newType;

}
