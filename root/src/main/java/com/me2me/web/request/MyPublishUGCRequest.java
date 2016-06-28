package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :10:59
 */
public class MyPublishUGCRequest extends Request {

    @Getter
    @Setter
    private int sinceId;

    @Getter
    @Setter
    private long customerId;
}
