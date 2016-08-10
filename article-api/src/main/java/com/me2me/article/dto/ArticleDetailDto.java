package com.me2me.article.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/18.
 */
@Data
public class ArticleDetailDto implements BaseEntity {

    private String title;

    private String thumb;

    private String content;

    private Date createTime;

    private String author;

    private String tags;

    private long type;

    private long id;

}
