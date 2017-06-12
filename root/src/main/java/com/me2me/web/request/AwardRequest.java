package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by 马秀成 on 2016/10/17.
 */
public class AwardRequest extends Request {

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private int activityName;

    @Getter
    @Setter
    private String mobile;

    @Getter
    @Setter
    private int awardId;

    @Getter
    @Setter
    private String awardName;

}
