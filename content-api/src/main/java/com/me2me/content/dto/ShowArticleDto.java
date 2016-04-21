package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/20.
 */
@Data
public class ShowArticleDto implements BaseEntity {

    private List<ArticleElement> result = Lists.newArrayList();

    public ArticleElement createArticleElement(){
        return new ArticleElement();
    }

    @Data
    public static class ArticleElement implements BaseEntity{
        private long id;

        private String title;

        private String content;

        private String thumb;
    }

}
