package com.me2me.article.dao;

import com.google.common.collect.Lists;
import com.me2me.article.dto.FeedDto;
import com.me2me.article.mapper.ArticleMapper;
import com.me2me.article.mapper.ArticleTypeMapper;
import com.me2me.article.model.Article;
import com.me2me.article.model.ArticleExample;
import com.me2me.article.model.ArticleType;
import com.me2me.article.model.ArticleTypeExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/17.
 */
@Repository
public class ArticleMybatisDao {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTypeMapper articleTypeMapper;

    public List<Article> articleTimeline(){
        ArticleExample example = new ArticleExample();
        example.setOrderByClause("create_time desc");
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public void save(Article article){
        articleMapper.insertSelective(article);
    }

    public List<ArticleType> loadArticleTypes(){
        ArticleTypeExample example = new ArticleTypeExample();
        return articleTypeMapper.selectByExample(example);
    }

    public Article loadArticleById(long id){
        return articleMapper.selectByPrimaryKey(id);
    }

    public List<Article> getArticleByType(long type){
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andArticleTypeEqualTo(type);
        example.setOrderByClause(" id desc limit 15");
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public List<Article> getArticleTop10(){
        ArticleExample example = new ArticleExample();
        example.setOrderByClause(" rand() limit 10");
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public List<Article> getHotArticle10(){
        ArticleExample example = new ArticleExample();
        example.setOrderByClause(" rand() limit 10");
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public List<Article> getGuess10(){
        ArticleExample example = new ArticleExample();
        example.setOrderByClause(" rand() limit 10");
        return articleMapper.selectByExampleWithBLOBs(example);
    }

}
