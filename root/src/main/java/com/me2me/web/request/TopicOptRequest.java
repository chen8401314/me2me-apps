package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/21
 */
public class TopicOptRequest {

    @Getter
    @Setter
    private long uid;

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private int action;

}
