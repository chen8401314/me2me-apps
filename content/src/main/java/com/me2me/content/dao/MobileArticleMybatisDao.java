package com.me2me.content.dao;

import com.me2me.content.mapper.MobileArticleMapper;
import com.me2me.content.model.MobileArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/20.
 */
@Repository
public class MobileArticleMybatisDao {

    @Autowired
    private MobileArticleMapper mobileArticleMapper;

    public List<MobileArticle> showArticle(int sinceId){
        return null;//mobileArticleMapper.showArticleBySinceId(sinceId);
    }

}
