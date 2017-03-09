package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
public class WapxIosRequest {

    @Getter
    @Setter
    private String udid;

    @Getter
    @Setter
    private String app;

    @Getter
    @Setter
    private String idfa;

    @Getter
    @Setter
    private String openudid;

    @Getter
    @Setter
    private String os;

    @Getter
    @Setter
    private String callbackurl;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private int status;

}
