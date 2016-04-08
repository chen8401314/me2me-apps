package com.me2me.admin.web;

import com.me2me.admin.web.request.ContentForwardRequest;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentH5Dto;
import com.me2me.content.service.ContentService;
import com.plusnet.common.util.StringEscapeUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
@RequestMapping("/console")
@Controller
public class Console  {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "/data")
    public ModelAndView data(){
        ModelAndView mv = new ModelAndView("data");
        List<String> list = new ArrayList<String>();
        for(int i = 0;i<100; i++){
            list.add(i+"");
        }
        mv.addObject("data",list);
        return mv;
    }

    @RequestMapping(value = "/{viewName}")
    public String publish(@PathVariable("viewName") String viewName){
        return viewName;
    }

    @RequestMapping(value = "/forward")
    public ModelAndView forward(ContentForwardRequest request){
        ModelAndView mv = new ModelAndView("forward");
        ContentH5Dto content = contentService.getContent(request.getId());
        if(content.getType()!= Specification.ArticleType.EDITOR.index) {
            // 处理特殊字符
            String cx = content.getContent();
            cx = StringEscapeUtil.escapeHtml(cx);
            cx = cx.replace("\n", "<br/>");
            content.setContent(cx);
        }
        mv.addObject("root",content);
        return mv;
    }
}
