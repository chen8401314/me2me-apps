package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc41 on 2016/9/12.
 */
public class ThirdPartRequest {

    /**
     * 第三方登录OPENID
     */
    @Getter
    @Setter
    private String thirdPartOpenId;

    /**
     * 第三方登录token
     */
    @Getter
    @Setter
    private String thirdPartToken;

    /**
     * 第三方头像
     */
    @Getter
    @Setter
    private String avatar;

    /**
     * 第三方登录类型 QQ：1 ，微信： 2，新浪微博：3。
     */
    @Getter
    @Setter
    private int thirdPartType;

    @Getter
    @Setter
    private String nickName;

}
