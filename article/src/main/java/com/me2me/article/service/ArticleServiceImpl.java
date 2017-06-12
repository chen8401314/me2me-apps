package com.me2me.article.service;
import com.me2me.article.dao.ArticleMybatisDao;
import com.me2me.article.dto.ArticleDetailDto;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.dto.CreateArticleDto;
import com.me2me.article.dto.FeedDto;
import com.me2me.article.model.AlbumImage;
import com.me2me.article.model.AlbumImageExample;
import com.me2me.article.model.Article;
import com.me2me.article.model.ArticleType;
import com.me2me.common.page.Page;
import com.me2me.common.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMybatisDao articleMybatisDao;

    /**
     * 获取文章时间线
     * @param sinceId
     * @return
     */
    @Override
    public ArticleTimelineDto timeline(long sinceId) {
        ArticleTimelineDto articleTimelineDto = new ArticleTimelineDto();
        List<Article> list =  articleMybatisDao.articleTimeline();
        for(Article article : list){
            ArticleTimelineDto.ArticleTimelineElement element = articleTimelineDto.createElement();
            element.setAuthor("小编");
            element.setId(article.getId());
            element.setTitle(article.getArticleTitle());
            if(article.getArticleContent().length()>200){
                element.setContent(article.getArticleContent().substring(0,200) + "...");
            }else{
                element.setContent(article.getArticleContent());
            }
            element.setCreateTime(article.getCreateTime());
            element.setThumb(article.getArticleThumb());
            element.setTags("扯淡");
            articleTimelineDto.getElements().add(element);
        }
        return articleTimelineDto;
    }

    /**
     * 创建文章
     * @param createArticleDto
     */
    @Override
    public void createArticle(CreateArticleDto createArticleDto) {
        Article article = new Article();
        article.setArticleTitle(createArticleDto.getTitle());
        article.setArticleType(createArticleDto.getArticleType());
        article.setArticleContent(createArticleDto.getContent());
        article.setArticleThumb(createArticleDto.getThumb());
        articleMybatisDao.save(article);
    }

    /**
     * 获取文章类型
     * @return
     */
    @Override
    public List<ArticleType> getArticleTypes() {
        return articleMybatisDao.loadArticleTypes();
    }

    @Override
    public ArticleDetailDto getArticleById(long id) {
        ArticleDetailDto articleDetailDto = new ArticleDetailDto();
        Article article = articleMybatisDao.loadArticleById(id);
        articleDetailDto.setContent(article.getArticleContent());
        articleDetailDto.setThumb(article.getArticleThumb());
        articleDetailDto.setTitle(article.getArticleTitle());
        articleDetailDto.setAuthor("小编");
        articleDetailDto.setCreateTime(article.getCreateTime());
        articleDetailDto.setType(article.getArticleType());
        articleDetailDto.setId(article.getId());
        return articleDetailDto;
    }

    @Override
    public FeedDto getArticleByType() {
        FeedDto feedDto = new FeedDto();
        List<Article>  list = articleMybatisDao.getArticleByType(7);
        for (Article article : list){
            FeedDto.ImageDto imageDto = feedDto.createImageDto();
            imageDto.setImage(article.getArticleThumb());
            imageDto.setTitle(article.getArticleTitle());
            imageDto.setId(article.getId());
            feedDto.getQuImages().add(imageDto);
        }
        List<Article>  list2 = articleMybatisDao.getArticleByType(22);
        for (Article article : list2){
            FeedDto.ImageDto imageDto = feedDto.createImageDto();
            imageDto.setImage(article.getArticleThumb());
            imageDto.setTitle(article.getArticleTitle());
            imageDto.setId(article.getId());
            feedDto.getAlbums().add(imageDto);
        }
        List<Article>  list3 = articleMybatisDao.getArticleByType(8);
        for (Article article : list3){
            FeedDto.JokeDto jokeDto = feedDto.createJokeDto();
            jokeDto.setSummary(article.getArticleContent());
            jokeDto.setCoverImage(article.getArticleThumb());
            jokeDto.setTitle(article.getArticleTitle());
            jokeDto.setId(article.getId());
            feedDto.getJokes().add(jokeDto);
        }
        return feedDto;
    }

    @Override
    public List<Article> getArticleTop10() {
        return  articleMybatisDao.getArticleTop10();
    }

    @Override
    public List<Article> getHotArticle10() {
        return articleMybatisDao.getHotArticle10();
    }

    @Override
    public List<Article> getGuess10() {
        return articleMybatisDao.getGuess10();
    }

    @Override
    public List<AlbumImage> showAlbumImagesByAlbumId(long albumId) {
        return articleMybatisDao.showAlbumImagesByAlbumId(albumId);
    }

    @Override
    public Page<Article> showItemByType(int currentPage,long type) {
        // 每页显示20条数据
        return articleMybatisDao.showArticleByPage(currentPage,20,type);
    }

    public static void main(String[] args) {
        Random random = new Random();
        for(int i = 0;i<1000;i++){
            int value = random.nextInt(10);
            System.out.println(value);
        }


    }
}
