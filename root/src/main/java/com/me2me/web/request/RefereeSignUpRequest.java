package com.me2me.web.request;


import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/4
 * Time :22:19
 */
public class RefereeSignUpRequest extends Request {
    @Getter
    @Setter
    private String mobile;

    @Getter
    @Setter
    private String encrypt;

    @Getter
    @Setter
    private String confirmEncrypt;

    @Getter
    @Setter
    private int gender;

    @Getter
    @Setter
    private int years;

    @Getter
    @Setter
    private int start;

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private String deviceNo;

    @Getter
    @Setter
    private int platform;

    @Getter
    @Setter
    private String os;

    @Getter
    @Setter
    private String introduced;

    @Getter
    @Setter
    private long refereeUid;
}
