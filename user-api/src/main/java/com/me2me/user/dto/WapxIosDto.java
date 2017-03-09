package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
@Data
public class WapxIosDto implements BaseEntity {

    private String udid;

    private String app;

    private String idfa;

    private String openudid;

    private String os;

    private String callbackurl;

    private String ip;

    private int status;

}
