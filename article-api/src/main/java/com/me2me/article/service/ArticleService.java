package com.me2me.article.service;

import com.me2me.article.dto.ArticleDetailDto;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.dto.CreateArticleDto;
import com.me2me.article.model.ArticleType;
import com.me2me.common.web.Response;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface ArticleService {

    ArticleTimelineDto timeline(long sinceId);

    void createArticle(CreateArticleDto createArticleDto);

    List<ArticleType> getArticleTypes();

    ArticleDetailDto getArticleById(long id);

}
