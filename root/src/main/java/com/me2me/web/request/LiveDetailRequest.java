package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Fifteen on 2016/10/24.
 */
public class LiveDetailRequest extends Request{
    @Getter
    @Setter
    private int offset;
    
    @Getter
    @Setter
    private int pageNo;

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private long sinceId;
    
    @Getter
    @Setter
    private int direction;
    
    @Getter
    @Setter
    private int reqType;
}
