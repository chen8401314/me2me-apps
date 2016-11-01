package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc188 on 2016/11/1.
 */
public class GagRequest extends Request{
    @Setter
    @Getter
    private long uid;

    @Setter
    @Getter
    private long targetUid;

    @Setter
    @Getter
    private int type;

    @Setter
    @Getter
    private long cid;

    @Setter
    @Getter
    private int gagLevel;
}
