package com.me2me.article.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/17.
 */
@Data
public class ArticleTimelineDto implements BaseEntity {


    private List<ArticleTimelineElement> elements = Lists.newArrayList();

    public ArticleTimelineElement createElement(){
        return new ArticleTimelineElement();
    }

    @Data
    public class ArticleTimelineElement implements BaseEntity{

        private long id;

        private String title;

        private String thumb;

        private String content;

        private Date createTime;

        private String author;

        private String tags;

        private String summary;

    }

}
