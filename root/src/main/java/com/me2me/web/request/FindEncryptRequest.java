package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/24
 * Time :17:31
 */
public class FindEncryptRequest {

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String firstEncrypt;

    @Getter
    @Setter
    private String secondEncrypt;
}
