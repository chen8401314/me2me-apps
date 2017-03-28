package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/28
 */
public class DaoDaoRequest {

    @Getter
    @Setter
    private int appid;

    @Getter
    @Setter
    private String idfa;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private String callback;

}
