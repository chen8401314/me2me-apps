package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc188 on 2016/10/20.
 */
public class EditSpeakRequest extends Request{

    @Getter
    @Setter
    private long fragmentId;

    @Getter
    @Setter
    private String extra;
}
