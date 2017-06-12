package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/2/4
 */
public class SettingModifyRequest extends Request {

    @Getter
    @Setter
    private int action;

    @Getter
    @Setter
    private String params;

    @Getter
    @Setter
    private long topicId;

}
