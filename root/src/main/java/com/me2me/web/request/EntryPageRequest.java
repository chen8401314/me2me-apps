package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc188 on 2016/11/3.
 */
public class EntryPageRequest extends Request{
    @Setter
    @Getter
    private int cversion;

    @Setter
    @Getter
    private  int type;
}
