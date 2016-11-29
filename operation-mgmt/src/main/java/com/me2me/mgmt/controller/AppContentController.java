package com.me2me.mgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ShowUserContentsDTO;
import com.me2me.content.dto.UserContentSearchDTO;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.UserContentQueryDTO;

@Controller
@RequestMapping("/appcontent")
public class AppContentController {

	@Autowired
    private ContentService contentService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/init/{uid}")
	public ModelAndView queryinit(@PathVariable long uid){
		UserContentQueryDTO dto = new UserContentQueryDTO();
		
		UserContentSearchDTO searchDTO = new UserContentSearchDTO();
		searchDTO.setPage(1);
		searchDTO.setPageSize(10);
		searchDTO.setUid(uid);
		
		//文章评论
		searchDTO.setSearchType(Specification.UserContentSearchType.ARTICLE_REVIEW.index);
		Response resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto.setArticleReviewDTO((ShowUserContentsDTO)resp.getData());
		}
		
		//ugc
		searchDTO.setSearchType(Specification.UserContentSearchType.UGC.index);
		resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto.setUgcDTO((ShowUserContentsDTO)resp.getData());
		}
		
		//ugc评论
		searchDTO.setSearchType(Specification.UserContentSearchType.UGC_OR_PGC_REVIEW.index);
		resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto.setUgcReviewDTO((ShowUserContentsDTO)resp.getData());
		}
		
		//王国
		searchDTO.setSearchType(Specification.UserContentSearchType.KINGDOM.index);
		resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto.setTopicDTO((ShowUserContentsDTO)resp.getData());
		}
		
		//王国发言/评论
		searchDTO.setSearchType(Specification.UserContentSearchType.KINGDOM_SPEAK.index);
		resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto.setTopicFragmentDTO((ShowUserContentsDTO)resp.getData());
		}
		
		ModelAndView view = new ModelAndView("appcontent/userContentList");
		view.addObject("dataObj",dto);
		return view;
	}
}
