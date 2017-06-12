package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/21
 */
public class TopicOptRequest extends Request{

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private int action;

}
