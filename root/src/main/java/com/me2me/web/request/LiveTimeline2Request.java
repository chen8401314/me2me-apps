package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/25
 * Time :13:26
 */
public class LiveTimeline2Request extends Request{

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private int sinceId;

    @Getter
    @Setter
    private int direction;

    @Getter
    @Setter
    private int first;

    @Getter
    @Setter
    private int mode;

    @Getter
    @Setter
    private int forms;
}
