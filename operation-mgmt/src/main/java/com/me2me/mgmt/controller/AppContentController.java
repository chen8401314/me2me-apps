package com.me2me.mgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
		dto.setUid(uid);
		
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
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query")
	@ResponseBody
	public String query(long uid, int type, int page, int pageSize){
		
		UserContentSearchDTO searchDTO = new UserContentSearchDTO();
		searchDTO.setPage(page);
		searchDTO.setPageSize(pageSize);
		searchDTO.setUid(uid);
		searchDTO.setSearchType(type);//1文章评论，2UGC，3UGC评论，4王国，5王国评论/发言
		Response resp = contentService.searchUserContent(searchDTO);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			JSONObject obj = (JSONObject)JSON.toJSON(resp.getData());
			return obj.toJSONString();
		}else{
			return "{}";
		}
	}
	
	@RequestMapping(value = "/del/{type}/{id}")
	@ResponseBody
	public String delContent(@PathVariable int type, @PathVariable long id){
		contentService.delUserContent(type, id);
		return "0";
	}
}
