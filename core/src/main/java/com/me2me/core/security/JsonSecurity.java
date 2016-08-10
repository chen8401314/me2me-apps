package com.me2me.core.security;

import lombok.Data;

import java.io.Serializable;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/5.
 */
@Data
public class JsonSecurity implements Serializable {

    private String appId;

    private String currentTime;

    private String nonce;

    private String sign;



}
