package com.me2me.common.web;

import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
public abstract class Request {

    @Getter
    @Setter
    private long uid;
    @Getter
    @Setter
    private String token;
    @Getter
    @Setter
    private String version;

}
