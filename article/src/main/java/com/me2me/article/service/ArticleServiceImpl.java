package com.me2me.article.service;
import com.me2me.article.dao.ArticleMybatisDao;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.model.Article;
import com.me2me.common.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMybatisDao articleMybatisDao;

    @Override
    public ArticleTimelineDto timeline(long sinceId) {
        ArticleTimelineDto articleTimelineDto = new ArticleTimelineDto();
        List<Article> list =  articleMybatisDao.articleTimeline();
        for(Article article : list){
            ArticleTimelineDto.ArticleTimelineElement element = articleTimelineDto.createElement();
            element.setAuthor("小编");
            element.setTitle(article.getArticleTitle());
            element.setContent(article.getArticleContent());
            element.setCreateTime(article.getCreateTime());
            element.setSummary(article.getArticleSummary());
            element.setThumb(article.getArticleThumb());
            element.setTags("扯淡");
            articleTimelineDto.getElements().add(element);
        }
        return articleTimelineDto;
    }
}
