package com.me2me.content.widget;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public class PublishFactory {

    public static Publish getInstance(int type){
        Publish instance = null;
        if(type==1){
            instance = new ForwardPublish();
        }else if(type==2){
            instance = new PublishLive();
        }else if(type==3){
            instance = new PublishUGC();
        }else{
            throw new RuntimeException("参数非法....");
        }
        return instance;
    }
}
