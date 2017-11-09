package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc41 on 2016/9/12.
 */
public class ThirdPartRequest extends Request {

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

    @Getter
    @Setter
    private int gender;

    @Getter
    @Setter
    private String jPushToken;

    @Getter
    @Setter
    private String mobile;

    @Getter
    @Setter
    private String encrypt;

    @Getter
    @Setter
    private String unionId;

    @Getter
    @Setter
    private int h5type;

    @Getter
    @Setter
    private String newNickName;

    @Getter
    @Setter
    private int platform;

    @Getter
    @Setter
    private String params;
    
    @Getter
    @Setter
    private String openinstallData;
    
    @Getter
    @Setter
    private String deviceData;
    
    @Getter
    @Setter
    private long fromUid;
    
    @Getter
    @Setter
    private long fromTopicId;
    
    @Getter
    @Setter
    private String hwToken;
}
