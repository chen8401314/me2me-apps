package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/4
 * Time :22:31
 */
@Data
public class UserRefereeSignUpDto implements BaseEntity{

    private String mobile ;

    private String encrypt ;

    private int gender ;

    private int star;

    private String nickName;

    private String deviceNo;

    private int platform;

    private String os;

    private String introduced;

    private long refereeUid;
}
