package com.me2me.admin.web;

import com.me2me.admin.web.request.TimelineRequest;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.service.ArticleService;
import com.me2me.admin.web.request.ContentForwardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
// @RequestMapping("/home")
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
        ModelAndView mv = new ModelAndView("index");
        ArticleTimelineDto content = articleService.timeline(request.getSinceId());
        mv.addObject("root",content);
        return mv;
    }

}
