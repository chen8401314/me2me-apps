package com.me2me.content.service;

import com.me2me.common.web.Response;
import com.me2me.content.dao.MobileArticleMybatisDao;
import com.me2me.content.dto.ShowArticleDto;
import com.me2me.content.model.MobileArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class MobileArticleServiceImpl implements MobileArticleService {

    @Autowired
    private MobileArticleMybatisDao mobileArticleMybatisDao;


    @Override
    public Response showArticle(int sinceId) {
        List<MobileArticle> lists =  mobileArticleMybatisDao.showArticle(sinceId);
        ShowArticleDto showArticleDto = new ShowArticleDto();
        for(MobileArticle mobileArticle : lists){
            ShowArticleDto.ArticleElement element = showArticleDto.createArticleElement();
            element.setId(mobileArticle.getId());
            element.setContent(mobileArticle.getContent());
            element.setThumb(mobileArticle.getThumb());
            element.setTitle(mobileArticle.getTitle());
            showArticleDto.getResult().add(element);
        }
        return Response.success(200,"get data success",showArticleDto);
    }
}
