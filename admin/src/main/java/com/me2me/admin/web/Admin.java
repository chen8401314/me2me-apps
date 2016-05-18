package com.me2me.admin.web;

import com.me2me.admin.web.request.LoginRequest;
import com.me2me.admin.web.request.TimelineRequest;
import com.me2me.article.dto.ArticleTimelineDto;
import com.me2me.article.service.ArticleService;
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
@RequestMapping("/admin")
@Controller
public class Admin {

    @Autowired
    private ArticleService articleService;


    @RequestMapping(value = "/{viewName}")
    public String publish(@PathVariable("viewName") String viewName){
        return viewName;
    }

    @RequestMapping(value = "/login")
    public ModelAndView index(LoginRequest request){
        ModelAndView mv = new ModelAndView("admin/index");
        if(request.getUserName().equals("kisszpy")&&request.getEncrypt().equals("xiaolangbi1250")){
            return mv;
        }else{
            mv.setViewName("error");
            return mv;
        }
    }
    @RequestMapping(value = "/article/manage")
    public ModelAndView articleManage(LoginRequest request){
        ModelAndView mv = new ModelAndView("admin/article_manage");
        return mv;
    }
    
}
