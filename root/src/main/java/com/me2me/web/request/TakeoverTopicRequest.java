package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author:陈翔
 * Date: 2017/7/3
 * Time :9:22
 */
public class TakeoverTopicRequest extends Request{

    @Setter
    @Getter
    private long topicId;
}
