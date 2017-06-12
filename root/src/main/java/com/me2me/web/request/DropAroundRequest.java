package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/17
 */
public class DropAroundRequest extends Request {

    @Getter
    @Setter
    private long sourceTopicId;

}
