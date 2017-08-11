package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc329 on 2017/4/5.
 */
public class MobileLiveDetailRequest extends Request {

    @Getter
    @Setter
    private long topicId; // 王国ID
    @Getter
    @Setter
    private long sinceId;
    @Getter
    @Setter
    private int offset;
    @Getter
    @Setter
    private int direction;
    @Getter
    @Setter
    private int pageNo;
    @Getter
    @Setter
    private Long fromUid;
}
