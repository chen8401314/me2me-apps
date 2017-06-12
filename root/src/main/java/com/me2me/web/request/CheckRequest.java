package com.me2me.web.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc41 on 2016/9/13.
 */
public class CheckRequest {

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private String openId;

    @Getter
    @Setter
    private String unionId;

    @Getter
    @Setter
    private int thirdPartType;

}
