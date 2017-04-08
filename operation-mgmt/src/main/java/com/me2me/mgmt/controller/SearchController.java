package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.search.service.SearchService;
import com.plusnet.sso.api.vo.JsonResult;

import lombok.extern.slf4j.Slf4j;

@Controller  
@RequestMapping("/search")
@Slf4j
public class SearchController {
		
	@Autowired
	private  SearchService searchService;
	
	@RequestMapping("/console")
	public String console(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String key = request.getParameter("key");
		key= new String(key.getBytes("iso-8859-1"),"utf-8");
		//searchService.search(keyword, page, pageSize, uid, isSearchFans)
		return "search/console";
	}
	
	@ResponseBody
	@RequestMapping("/startTask")
	public JsonResult startTask(HttpServletRequest request,HttpServletResponse response){
		try{
			String task= request.getParameter("task");
			if("ugc".equals(task)){
				searchService.indexUgcData(true);
			}else if("kingdom".equals(task)){
				searchService.indexKingdomData(true);
			}else if("user".equals(task)){
				searchService.indexUserData(true);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return JsonResult.error(e.getMessage());
		}
		return JsonResult.success();
	}
	
}
