package com.me2me.content.widget;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:04
 */
public class LikesFactory {

    public static Likes getInstance(int type){
        Likes instance = null;
        if(type == Specification.LikesType.CONTENT.index){
            instance = SpringContextHolder.getBean(ContentLikes.class);
        }else if(type == Specification.LikesType.LIVE.index){
            instance = SpringContextHolder.getBean(LiveLikes.class);
        }else if(type == Specification.LikesType.ARTICLE.index){
            instance = SpringContextHolder.getBean(ArticleLikes.class);
        }else if(type == Specification.LikesType.ACTIVITY.index){
            instance = SpringContextHolder.getBean(ActivityLikes.class);
        }
        else{
            throw new RuntimeException("参数非法....");
        }
        return instance;
    }
}
