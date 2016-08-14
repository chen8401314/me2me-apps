package com.me2me.sms.service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/13.
 */
public enum ChannelType{
    NORMAL_SMS(1,"短信验证方式"),

    VOICE_SMS(2,"语音验证方式"),

    NET_CLOUD_SMS(3,"网易云信");

    public int index;

    public String name;

    ChannelType(int index,String name){
        this.index = index;
        this.name = name;
    }
}
