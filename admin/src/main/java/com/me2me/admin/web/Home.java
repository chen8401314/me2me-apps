package com.me2me.admin.web;

import com.me2me.admin.web.request.ArticleDetailRequest;
import com.me2me.admin.web.request.ShowItemRequest;
import com.me2me.admin.web.request.TimelineRequest;
import com.me2me.article.dto.ArticleDetailDto;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.dto.FeedDto;
import com.me2me.article.model.AlbumImage;
import com.me2me.article.model.Article;
import com.me2me.article.service.ArticleService;
import com.me2me.admin.web.request.ContentForwardRequest;
import com.me2me.common.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
@Controller
public class Home {

    @Autowired
    private ArticleService articleService;


    @RequestMapping(value = "/{viewName}")
    public String publish(@PathVariable("viewName") String viewName){
        return viewName;
    }

    @RequestMapping(value = "/")
    public ModelAndView index(TimelineRequest request){
        // 精选段子10条
        // 趣图 15条
        // 精选图片 15条
        ModelAndView mv = new ModelAndView("index");
        FeedDto feedDto = articleService.getArticleByType();
        mv.addObject("root",feedDto);
        List<Article> topTen = articleService.getArticleTop10();
        List<Article> hotTen = articleService.getHotArticle10();
        mv.addObject("topTen",topTen);
        mv.addObject("hotTen",hotTen);
        return mv;
    }

    @RequestMapping(value = "/show_detail")
    public ModelAndView showDetail(ArticleDetailRequest request){
        ModelAndView mv = new ModelAndView("show_detail");
        ArticleDetailDto article = articleService.getArticleById(request.getId());
        mv.addObject("root",article);
        List<Article> guessTen = articleService.getGuess10();
        mv.addObject("guessTen",guessTen);
        if(article.getType()==22) {
            List<AlbumImage> albumImages = articleService.showAlbumImagesByAlbumId(article.getId());
            mv.addObject("albumImages", albumImages);
        }
        return mv;
    }

    @RequestMapping(value = "/show_item")
    public ModelAndView showItem(ShowItemRequest request){
        ModelAndView mv = new ModelAndView("show_item");
        Page<Article> page= articleService.showItemByType(request.getCurrentPage(),request.getType());
        mv.addObject("root",page);
        mv.addObject("type",request.getType());
        List<Article> guessTen = articleService.getGuess10();
        mv.addObject("guessTen",guessTen);
        return mv;
    }



}
