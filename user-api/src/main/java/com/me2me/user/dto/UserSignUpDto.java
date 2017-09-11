package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Data
public class UserSignUpDto implements BaseEntity {

    private String mobile ;

    private String encrypt ;

    private int gender ;

    private int star;

    private String nickName;

    private String deviceNo;

    private int platform;

    private String os;

    private String introduced;

    private String channel;

    private String registerVersion;

    private String params;

    private int spreadChannel;

    private String verifyCode;
    
    private String openinstallData;
    
    private String deviceData;
    
    private String ip;
}
