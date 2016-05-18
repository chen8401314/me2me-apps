package com.me2me.article.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/17.
 */
@Data
public class CreateArticleDto implements BaseEntity {

    private String title;

    private String content;

    private String thumb;

    private long articleType;

}
