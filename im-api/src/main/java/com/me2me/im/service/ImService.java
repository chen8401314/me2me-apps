package com.me2me.im.service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface ImService {

    /**
     * 注册用户到im server 中
     */
    void register();

    /**
     * 用户登录到im server 中
     */
    void login();


    void sendMessage();

}
