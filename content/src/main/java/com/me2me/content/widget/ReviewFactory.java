package com.me2me.content.widget;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :17:43
 */
public class ReviewFactory {

    public static Review getInstance(int type){
        Review instance = null;
        if(type == Specification.ReviewType.CONTENT.index){
            instance = SpringContextHolder.getBean(ContentReview.class);
        }else if(type == Specification.ReviewType.ARTICLE.index){
            instance = SpringContextHolder.getBean(ArticleReview.class);
        }else if(type == Specification.ReviewType.ACTIVITY.index){
            instance = SpringContextHolder.getBean(ActivityReview.class);
        }else{
            throw new RuntimeException("参数非法....");
        }
        return instance;
    }
}
