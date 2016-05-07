package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :11:31
 */
public class ModifyUserProfileRequest extends Request{

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private int gender;

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private long yearsId;

    @Getter
    @Setter
    private long startId;

    @Getter
    @Setter
    private long socialClass;

    @Getter
    @Setter
    private long industry;

    @Getter
    @Setter
    private long marriageStatus;

    @Getter
    @Setter
    private long bearStatus ;

    @Getter
    @Setter
    private String avatar;

    @Getter
    @Setter
    private String birthday;

    @Getter
    @Setter
    private String hobby;

}
