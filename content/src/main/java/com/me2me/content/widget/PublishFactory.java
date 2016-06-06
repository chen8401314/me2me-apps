package com.me2me.content.widget;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public class PublishFactory {

    public static Publish getInstance(int type){
        Publish instance = null;
        if(type == Specification.ArticleType.ORIGIN.index){
            instance = SpringContextHolder.getBean(PublishUGC.class);
        }else if(type == Specification.ArticleType.LIVE.index){
            instance = SpringContextHolder.getBean(PublishLive.class);
        }else if(type == Specification.ArticleType.FORWARD.index){
            instance = SpringContextHolder.getBean(ForwardPublish.class);
        }else{
            throw new RuntimeException("参数非法....");
        }
        return instance;
    }
}
