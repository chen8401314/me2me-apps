package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Data
public class UserLoginDto implements BaseEntity {

    private String userName;

    private String encrypt;

    private int platform;

    private String os;

    private String deviceNo;

    private String jPushToken;

    private String verifyCode;
    
    private String channel;
    
    private String registerVersion;

    private String deviceData;
    
    private String ip;
}
