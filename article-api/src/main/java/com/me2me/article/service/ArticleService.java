package com.me2me.article.service;

import com.me2me.article.dto.ArticleDetailDto;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.dto.CreateArticleDto;
import com.me2me.article.dto.FeedDto;
import com.me2me.article.model.AlbumImage;
import com.me2me.article.model.Article;
import com.me2me.article.model.ArticleType;
import com.me2me.common.page.Page;

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

    // 精选段子8

    // 趣图7

    // 精选美图22

    FeedDto getArticleByType();


    List<Article> getArticleTop10();

    List<Article> getHotArticle10();

    List<Article> getGuess10();

    /**
     * 获取影集列表
     * @param albumId
     * @return
     */
    List<AlbumImage> showAlbumImagesByAlbumId(long albumId);


    Page<Article> showItemByType(int currentPage,long type);





}
