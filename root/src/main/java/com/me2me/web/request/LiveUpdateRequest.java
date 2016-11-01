package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc188 on 2016/10/31.
 */
public class LiveUpdateRequest extends Request{

    @Setter
    @Getter
    private long sinceId;

    @Setter
    @Getter
    private int offset;

    @Setter
    @Getter
    private long topicId;

}
