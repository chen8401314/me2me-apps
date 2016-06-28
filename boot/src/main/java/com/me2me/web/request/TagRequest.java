package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc62 on 2016/3/25.
 */
public class TagRequest extends Request{

    @Getter
    @Setter
    private long fromUid;

    @Getter
    @Setter
    private String tag;

    @Getter
    @Setter
    private long targetUid;
}
