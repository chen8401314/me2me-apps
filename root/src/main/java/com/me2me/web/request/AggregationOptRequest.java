package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/2/5
 */
public class AggregationOptRequest {

    @Getter
    @Setter
    private long uid;

    @Getter
    @Setter
    private int action;

    @Getter
    @Setter
    private long ceTopicId;

    @Getter
    @Setter
    private long acTopicId;

    @Getter
    @Setter
    private int type;

    @Getter
    @Setter
    private int applyId;

}
