package com.me2me.content.widget;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:43
 */
public class WriteTagFactory {

    public static WriteTag getInstance(int type){
        WriteTag instance = null;
        if(type == Specification.WriteTagType.ARTICLE.index){
            instance = SpringContextHolder.getBean(ArticleWriteTag.class);
        }else if(type == Specification.WriteTagType.ACTIVITY.index){
            instance = SpringContextHolder.getBean(ActivityWriteTag.class);
        }else if(type == Specification.WriteTagType.CONTENT.index){
            instance = SpringContextHolder.getBean(ContentWriteTag.class);
        }else{
            throw new RuntimeException("参数非法....");
        }
        return instance;
    }
}
