package com.me2me.sms.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/4/1
 */
@Data
public class ImUserInfoDto implements BaseEntity {

    private int code;

    private String token;

    private String userId;

}
