package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc188 on 2016/9/26.
 */
public class DisplayProtocolRequest extends Request {
    @Setter
    @Getter
    private int vLv;
}
