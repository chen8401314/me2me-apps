package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.dto.ShowContentDto;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.KeywordPageRequest;

@Controller
@RequestMapping("/ugc")
public class UgcController {
	
	@Autowired
    private ContentService contentService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/query")
    public ModelAndView query(KeywordPageRequest request){
		ModelAndView view = new ModelAndView("article/ugcList");
		EditorContentDto editorContentDto = new EditorContentDto();
        editorContentDto.setArticleType(0);//ugc
        editorContentDto.setPage(1);
        editorContentDto.setPageSize(500);
        String keyword = request.getKeyword();
        if(null == keyword){
        	keyword = "";
        }
        editorContentDto.setKeyword(keyword);
        Response resp = contentService.showContents(editorContentDto);
        if(null != resp && resp.getCode() == 200){
        	ShowContentDto data = (ShowContentDto)resp.getData();
        	view.addObject("dataObj", data);
        }
		return view;
	}
	
	@RequestMapping(value="/option/hot")
	public ModelAndView optionHot(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long ugcId = Long.valueOf(req.getParameter("i"));
    	
    	contentService.option(ugcId,0,action);
		
		ModelAndView view = new ModelAndView("redirect:/ugc/query");
    	return view;
	}
	
	@RequestMapping(value="/option/top")
	public ModelAndView optionTop(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long ugcId = Long.valueOf(req.getParameter("i"));
    	
    	ContentDto contentDto = new ContentDto();
    	contentDto.setAction(1);//设置/取消置顶
    	contentDto.setId(ugcId);
    	contentDto.setIsTop(action);
    	contentService.modifyPGC(contentDto);
		
		ModelAndView view = new ModelAndView("redirect:/ugc/query");
    	return view;
	}
}
