package com.me2me.user.dto;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/9/12.
 */
@Data
public class ThirdPartSignUpDto extends UserSignUpDto {

    /**
     * 第三方登录OPENID
     */
    private String thirdPartOpenId;

    /**
     * 第三方登录token
     */
    private String thirdPartToken;

    /**
     * 第三方头像
     */
    private String avatar;

    /**
     ** 第三方登录类型 QQ：1 ，微信： 2，新浪微博：3。
     */
    private int thirdPartType;

    private String jPushToken;

    /**
     * 数据绑定的标识
     */
    private long uid;

    private String unionId;

    //区分h5注册来源
    private int h5type;

    //新昵称 前台检查后的(h5登陆过次啊会有这个字段)
    private String newNickName;

    private int platform;

    private String channel;

    private String params;
    
    private String openinstallData;
    
    private String deviceData;
    
    private String ip;
    
    private long fromUid;//H5登录专用，传递分享来源用户ID
    
    private long fromTopicId;//H5登录专用，传递分享的来源王国ID

}
