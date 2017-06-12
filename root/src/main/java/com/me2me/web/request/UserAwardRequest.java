package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by 马秀成 on 2016/10/21.
 */
public class UserAwardRequest extends Request {

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private String awardName;

    @Getter
    @Setter
    private String mobile;
}
